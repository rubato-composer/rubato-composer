IMAGES_PNG=\
	composer.png \
	rubette.png \
	network.png \
	toolbar.png \
	mainwindow.png \
	messagelog.png \
	problemlog.png \
	newneticon.png \
	closeicon.png \
	networkpopup.png \
	note.png \
	runicon.png \
	runconticon.png \
	stopicon.png \
	viewtemplate.png \
	proptemplate.png \
	links.png \
	moduleentry.png \
	modulelist.png \
	formentry.png \
	formlist.png \
	denoentry.png \
	denoselect.png \
	slider.png \
	mmorphismentry.png \
	browser.png \
	scoreplay.png \
	voices.png \
	pathselect.png \
	simplerubette.png \
	addresseval.png \
	select2d.png \
	elemententry.png \
	modulebuilder.png \
	morphbuilder.png \
	simpledeno.png \
	limitdeno.png \
	colimitdeno.png \
	powerdeno.png \
	listdeno.png \
	limitform.png \
	powerform.png

IMAGES_PNG_EPS=$(IMAGES_PNG:.png=.eps)

IMAGES_EPS=$(IMAGES_PNG_EPS)

IMAGES_PNG_PDF=$(IMAGES_PNG:.png=.pdf)

IMAGES_PDF=$(IMAGES_PNG_PDF)

PS2PDF=ps2pdf $(PS2PDFOPTS)

PS2PDFOPTS+=-dPDFSETTINGS=/printer
PS2PDFOPTS+=-dCompatibilityLevel=1.4
PS2PDFOPTS+=-dMaxSubsetPct=100
PS2PDFOPTS+=-dUseFlateCompression=true
PS2PDFOPTS+=-dOptimize=true
PS2PDFOPTS+=-dEmbedAllFonts=true

.PHONY: clean manual.pdf manual.ps images_eps

all: manual.pdf

images_eps: $(IMAGES_EPS)

images_pdf: $(IMAGES_EPS) $(IMAGES_PDF)

manual.ps: manual.tex images_eps
	latex $<
	latex $<
	latex $<
	dvips manual.dvi -o $@

manual.pdf: manual.ps
	$(PS2PDF) manual.ps

%.eps: %.png
	bmeps -p3 -c -erf8 $^ $@

%.pdf: %.eps
	epstopdf $^

clean:
	rm -f *.log *.aux *.ps *.pdf *.dvi *.toc *.out $(IMAGES_PNG_EPS) $(IMAGES_PNG_PDF)
