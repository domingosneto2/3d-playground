# 3d-playground
A toy project to learn some 3D graphics concepts

My goal with this project was to implement all of the 3D logic myself instead of relying on OpenGL and the GPU.  This
was not intended to be production code, I just wanted dust off my linear algebra and re-learn some of these 3D graphics
skills.

I still use OpenGL to draw the triangles, but I do all conversions from Model space to World space to Camera Space to
Screen Space myself.  As expected, this is terribly inefficient, but that's ok.

I am testing this on a Dell XP 13 with the following specs:

* Processor	11th Gen Intel(R) Core(TM) i7-1185G7 @ 3.00GHz   3.00 GHz
* Installed RAM	32.0 GB (31.7 GB usable)

I max out 60fps with a mesh that has about 50K triangles (notice I do implement back face culling so I really
only draw half of those).  

There are still lots of optimizations I could implement to try and squeeze more performance out of this.
Obviously, the bottleneck here is the CPU, so for example I could implement a multi-threaded rendering pipeline
to take advantage of multiple cores.