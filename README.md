This is a library for calculating the laminar flow through a weighted graph. Any Set of Vertices that extends the Vertex interface can use this library.
Additionally there is an application that can be ran through the main class that lets you simulate electric circuits with voltage sources and resistors than
can calculate the flow through each component. 

The library currently offers the functions:

1. calculate the equivalent weight of all flowing loops through an edge. The method setPressure returns the equivalent weight
2. calculates the flow through all edges in a graph. You can set all the potential differences between two connected vertices
by calling the setPressure method and then calling the flowsThrough method to get the flows through all edges.
