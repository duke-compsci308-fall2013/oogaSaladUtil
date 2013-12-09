I modify the JGame so that it can support blending. Some effects such as fade in or fade out can be created.
To use it, now each JGObject still have a field called alpha( from 0 (total transparent) to 1(opaque). Just modifying
this alpha is enough. 
If you want to see effect, go to team foobar and use WordEffect class. It allows people to print 
fancy characters and these characters fade in then fade out. 