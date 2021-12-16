# MMDUtils
MMDUtils is a library for read/write mmd related file in java

# Features
[X] Read/Write VMD(Vocaloid Motion Data) file  
[ ] Read/Write PMX(Polygon Model eXtended) 2.0 file  
[ ] Read/Write PMD(Polygon Model Data) file  
[ ] Render model in LWJGL  
[ ] Process physics bone for model  
[ ] Display VMD animation

# PMD Format Support
We currently don't want to add support for PMD format, because [PMX file is better](https://learnmmd.com/bonus-pages/convert-pmd-models-pmx/) ~~and we are lazy~~.  
And you can [convert PMD to PMX](https://learnmmd.com/http:/learnmmd.com/convert-pmd-models-to-pmx-models/) to make it able to read.

# Credits
[MikuMikuFormats](https://github.com/oguna/MMDFormats) for read/write PMX and VMD files.  
[Saba](https://github.com/benikabocha/saba) for render model in LWJGL.