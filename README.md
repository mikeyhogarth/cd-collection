# cd-collection
Basic web app to learn some Angular, ScalaJS and Http4s. Uses Scala, Sqlest and H2 db for backend.

#To-do
1) Re-write JS with ScalaJS

2) Address issues with serialisation (non-standard characters). Probably want an alternative serialisation library.

3) Finish the other pages & services (albums)

4) Figure out how all the Angular dependencies work - do I need to check them into my repo or can I get SBT to handle it?

5) Handle window forms better (well, at all)

6) Add artist search function

#To run
Currently you have to manually create the tables in the H2 db (see SQL source for details)

To run from SBT:

~re-start 

to fire up server, then go to localhost:8080 (or most interesting page is localhost:8080/public/artists.html)