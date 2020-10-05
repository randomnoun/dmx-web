# What is it ?

This project contains a fairly primitive lighting Java webapp. 
Whilst running, it provides a webpage which can be used to define 

* DMX devices ( Enttec USB Pro / ArtNet / OpenDMX over COM / OpenDMX over JNI )
* a set of fixtures ( lights )
* fixture configurations ( stages ) 
* sound inputs ( audioSources )
* sound outputs ( audioControllers )
* a low-level control panel to set fixture values ( colour, strobe, macro, head angle / tilt )
* a high-level control panel to run user-defined threads which apply changes to those fixures over time (shows)
* a fairly unusable recording facility to create shows by manually setting up a sequence of 'frames' 
* an import/export capability to save and load your fixtures and shows from one installation to another

# Why is it ?

It was written back around 2010 when a mate of mine was running a comedy bar / restaurant 
and wanted to spruce things up a bit with some dynamic lighting, with an eye to opening a nightclub which never really got off the ground.

# Caveats

Looking back from the vantage-point of 2020 there's a few things that are a bit awful:

* it uses prototype.js as the javascript framework, which was still a thing back then. 
  If you're thinking of doing any further development on this, then you probably want to replace that with jQuery.
* to send lighting data back to the webpage, it uses 'comet', which was a term coined around 2010 
  for an IFRAME with a src containing a document that never finishes loading (\*) .
  That document contains a never-ending(\*) series of \<script>...\</script> tags which change the page state.
  These days you'd probably use websockets instead.
* it was designed to run on a 1024x768 resolution monitor and predated bootstrap, so the UI isn't even remotely responsive
* there's only a handful of fixtures defined, and the fixture framework only supports a limited set of properties
* there's only a handful of DMX devices / sound inputs and outputs, of varying levels of completeness
* and most of those require COM DLLs or other third-party software running to do the actual work
* there's no documentation whatsoever
* it's not very easy to get running
* new fixtures and shows and created using the Beanshell scripting language, which isn't as nice as using a WYSIWYG UI, 
  but arguably gives you a bit more flexibility. 
* there's a handful of fixures and shows in the java source code, but most examples of these 
  are sitting in a database on my local machine, so creating more of those without any documentation might be somewhat challenging.

# Caveats to the caveats

* (*) so those IFRAMEs do reload occasionally in a half-hearted attempt to prevent the browser from running out of memory.
