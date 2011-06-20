package org.rubato.scheme;

import java.util.LinkedList;
import java.util.List;

import org.rubato.scheme.Token.TokenType;
import org.rubato.util.TextUtils;


/**
 * This class provides the parser for Scheme expressions. The main
 * methods are {@link #parse()} and {@link #parse(String)}.
 * 
 * @author Gérard Milmeister
 */
public class Parser {

    /**
     * Creates a Scheme parser.
     */
    public Parser() {
        this.source = "()";
    }        


    /**
     * Creates a Scheme parser and initializes it with the given
     * source. 
     */
    public Parser(String source) {
        this.source = source;
    }        

    
    /**
     * Parses the specified source and returns a list of the
     * parsed expressions.
     */
    public List<SExpr> parse(String src) {
        this.source = src;
        return parse();
    }
    
    
    /**
     * Parses the initial source and returns a list of the
     * parsed expressions.
     */
    public List<SExpr> parse() {
        List<SExpr> list = new LinkedList<SExpr>();
        pos = 0;
        cur_token = nextToken();
        while (!isEOF()) {
            SExpr sexpr = parseSExpr();
            if (sexpr != null) {
                list.add(sexpr);
            }
            else {
                return null;
            }
        }
        return list;
    }
        
    
    private SExpr parseSExpr() {
        SExpr sexpr = null;
        switch (cur_token.type) {
        case LPAREN: {
            cur_token = nextToken();
            if (cur_token.type == TokenType.RPAREN) {
                sexpr = SExpr.NULL;
                cur_token = nextToken();
            }
            else {
                sexpr = parseList();
            }
            break;
        }
        case ARRAY: {
            cur_token = nextToken();
            if (cur_token.type == TokenType.RPAREN) {
                sexpr = new SVector(new SExpr[0]);
                cur_token = nextToken();
            }
            else {
                sexpr = parseVector();
            }
            break;
        }
        case COMPLEX: {
            sexpr = SComplex.make(cur_token.c);
            cur_token = nextToken();
            break;
        }
        case INTEGER: {
            sexpr = new SInteger(cur_token.i);
            cur_token = nextToken();
            break;
        }
        case RATIONAL: {
            sexpr = SRational.make(cur_token.r);
            cur_token = nextToken();
            break;
        }
        case REAL: {
            sexpr = SReal.make(cur_token.d);
            cur_token = nextToken();
            break;
        }
        case BOOLEAN: {
            sexpr = cur_token.b?SBoolean.TRUE:SBoolean.FALSE;
            cur_token = nextToken();
            break;
        }
        case CHAR: {
            sexpr = new SChar(cur_token.chr);
            cur_token = nextToken();
            break;
        }
        case SYMBOL: {
            sexpr = cur_token.symbol;
            cur_token = nextToken();
            break;
        }
        case STRING: {
            sexpr = new SString(cur_token.string);
            cur_token = nextToken();
            break;
        }
        case SQUOTE: {
            sexpr = parseQuote();
            break;
        }
        default: {
            setError("Unexpected token %1", cur_token);
            cur_token = nextToken();
        }
        }
        return sexpr;
    }
    
    
    private SExpr parseList() {
        if (cur_token.type == TokenType.RPAREN) {
            cur_token = nextToken();
            return SExpr.NULL;
        }
        else {
            SExpr car = parseSExpr();
            if (car != null) {
                if (cur_token.type == TokenType.DOT) {
                    cur_token = nextToken();
                    if (cur_token.type != TokenType.EOF) {
                        SExpr cdr = parseSExpr();
                        if (cdr != null) {
                            if (cur_token.type != TokenType.RPAREN) {
                                setError("Missing right parenthese");
                                return null;
                            }
                            else {
                                cur_token = nextToken();
                                return new SCons(car, cdr);
                            }
                        }
                        else {
                            return null;
                        }
                    }
                    else {
                        return null;
                    }
                }
                else {
                    SExpr cdr = parseList();
                    if (cdr != null) {
                        return new SCons(car, cdr);
                    }
                    else {
                        return null;
                    }
                }
            }
            else {
                return null;                
            }
        }
    }
    
    
    private SExpr parseVector() {
        List<SExpr> list = new LinkedList<SExpr>();
        SExpr element;
        while (cur_token.type != TokenType.EOF && cur_token.type != TokenType.RPAREN) {
            element = parseSExpr();
            if (element == null) {
                return null;
            }
            else {
                list.add(element);
            }
        }
        SExpr[] v = new SExpr[list.size()];
        int i = 0;
        for (SExpr e : list) {
            v[i++] = e;
        }
        if (cur_token.type == TokenType.EOF) {
            return null;
        }
        else {
            cur_token = nextToken();
            return new SVector(v);
        }
    }
    
    
    private SExpr parseQuote() {
        cur_token = nextToken();
        SExpr sexpr = parseSExpr();
        if (sexpr != null) {
            return new SCons(Token.QUOTE, new SCons(sexpr, SExpr.NULL));
        }
        else {
            return null;
        }
    }
    
    
    private boolean isEOF() {
        return cur_token.type == TokenType.EOF;
    }
    
        
    private Token nextToken() {
        Token token = new Token();
        skipWhiteSpace();
        if (pos >= source.length()) {
            token.type = TokenType.EOF;
            return token;
        }
        
        char c = source.charAt(pos);        
        if (c == '(') {
            token.type = TokenType.LPAREN;
            pos++;
        }
        else if (c == ')') {
            token.type = TokenType.RPAREN;
            pos++;
        }
        else if (c == '.') {
            token.type = TokenType.DOT;
            pos++;
        }
        else if (c == '\'') {
            token.type = TokenType.SQUOTE;
            pos++;
        }
        else if (Token.isSymbolStartChar(c)) {
            StringBuilder buf = new StringBuilder();
            buf.append(c);
            boolean eos = false;
            pos++;
            while (pos < source.length() && !eos) {
                c = source.charAt(pos);
                if (Token.isSymbolChar(c)) {
                    buf.append(c);
                    pos++;
                }
                else {
                    eos = true;
                }                
            }
            token.type = TokenType.SYMBOL;
            token.symbol = Symbol.make(buf.toString());
        }
        else if (Character.isDigit(c)) {
            StringBuilder buf = new StringBuilder();
            buf.append(c);
            boolean eon = false;
            pos++;
            while (pos < source.length() && !eon) {
                c = source.charAt(pos);
                if (Character.isDigit(c) || c == '/' || c == '.' || c == '+' || c == '-' || c == '*' || c =='i') {
                    buf.append(c);
                    pos++;
                }
                else {
                    eon = true;
                }                
            }
            token = Token.toNumberToken(buf.toString());
        }
        else if (c == '"') {
            StringBuilder buf = new StringBuilder();
            pos++;
            boolean eos = false;
            while (pos < source.length() && !eos) {
                c = source.charAt(pos);
                if (c == '"') {
                    eos = true;
                }
                else {
                    buf.append(c);
                }
                pos++;
            }
            token.type = TokenType.STRING;
            token.string = buf.toString();
        }
        else if (c == '#') {
            pos++;
            if (pos < source.length()) {
                c = source.charAt(pos++);
                if (c == 't') {
                    token.type = TokenType.BOOLEAN;
                    token.b = true;
                }
                else if (c == 'f') {
                    token.type = TokenType.BOOLEAN;
                    token.b = false;
                }
                else if (c == '\\') {
                    if (pos < source.length()) {
                        StringBuilder buf = new StringBuilder();
                        buf.append(source.charAt(pos++));
                        while (pos < source.length() && Character.isLetter(source.charAt(pos))) {
                            buf.append(source.charAt(pos++));
                        }
                        if (buf.length() == 1) {
                            token.type = TokenType.CHAR;
                            token.chr = buf.charAt(0);
                        }
                        else {
                            String s = buf.toString();
                            if (s.equals("space")) {
                                token.type = TokenType.CHAR;
                                token.chr = ' ';
                            }
                            else if (s.equals("newline")) {
                                token.type = TokenType.CHAR;
                                token.chr = '\n';
                            }
                            else {
                                token.type = TokenType.ERROR;
                            }
                        }
                    }
                    else {
                        token.type = TokenType.ERROR;
                    }
                }
                else if (c == '(') {
                    token.type = TokenType.ARRAY;
                }
                else {
                    token.type = TokenType.ERROR;
                }
            }
            else {
                token.type = TokenType.ERROR;
            }
        }
        else {
            token.type = TokenType.ERROR;
        }
        
        return token;
    }
    
    
    private void skipWhiteSpace() {
        while (pos < source.length()) {
            if (Character.isWhitespace(source.charAt(pos))) {
                pos++;
            }
            else if (source.charAt(pos) == ';') {
                pos++;
                while (pos < source.length() && source.charAt(pos) != '\n') {
                    pos++;
                }
            }
            else {
                return;
            }
        }
    }
    
    
    private void setError(String msg, Object ... objects) {
        error = TextUtils.replaceStrings(msg, objects);
    }
    
    
    /**
     * Removes all errors.
     */
    public void clearError() {
        error = null;
    }
    

    /**
     * Returns true iff the last parsing had an error.
     */
    public boolean hasError() {
        return error != null;
    }
    

    /**
     * Returns the error message of the last parsing.
     */
    public String getError() {
        return error;
    }
    
    
    private String error = null;
    
    private String source;
    private int    pos;   
    private Token  cur_token;
}
