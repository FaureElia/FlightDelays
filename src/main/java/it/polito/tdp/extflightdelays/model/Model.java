package it.polito.tdp.extflightdelays.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.Graphs;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;
import org.jgrapht.traverse.BreadthFirstIterator;

import it.polito.tdp.extflightdelays.db.ExtFlightDelaysDAO;

public class Model {
	//inizializzo gli elementi del grafo
	private Graph<Airport,DefaultWeightedEdge> grafo;
	private ExtFlightDelaysDAO dao;
	private Map<Integer,Airport> idMap;
	
	public Model() {
		this.grafo=new SimpleWeightedGraph<>(DefaultWeightedEdge.class);
		this.dao=new ExtFlightDelaysDAO();
		this.idMap=new HashMap<Integer,Airport>();
		this.dao.loadAllAirports(idMap);//popolo la idMap
	}
	
	public Set<Airport> creaGrafo(int nAirlines) {
		
		Graphs.addAllVertices(this.grafo, this.dao.getVertici(nAirlines, idMap));
		List<Rotta> edges=this.dao.getRotte(idMap);
		
		for (Rotta e:edges) {
			Airport origin=e.getOrigin();
			Airport destination=e.getDestination();
			int N=e.getN();
			if(this.grafo.containsVertex(destination) && this.grafo.containsVertex(origin) ) {
				if(grafo.containsEdge(origin, destination)){
					DefaultWeightedEdge edge=this.grafo.getEdge(origin, destination);
					this.grafo.setEdgeWeight(edge, this.grafo.getEdgeWeight(edge)+N);
				}else {
					DefaultWeightedEdge edge=this.grafo.addEdge(origin, destination);
					this.grafo.setEdgeWeight(edge, N);
					}
					
				}
		}
			
			
		System.out.println("ci sono : "+this.grafo.vertexSet().size()+" vertici");
		System.out.println("ci sono : "+this.grafo.edgeSet().size()+" archi");
		
		
		return this.grafo.vertexSet();
	}
	
	public List<Airport> verificaConnessione(Airport p,Airport d) {
		if (this.grafo.vertexSet().isEmpty()) {
			return null;
		}
		BreadthFirstIterator<Airport, DefaultWeightedEdge> iteratore=new BreadthFirstIterator<>(this.grafo,p);
		List<Airport> componenteConnessa=new ArrayList<>();
		boolean flag=true;
		
		while(flag && iteratore.hasNext()) {
			Airport a=iteratore.next();
			componenteConnessa.add(a);
			System.out.println(a);
			if(a.equals(d)) {
				flag=false;
			}
		}
		if (flag==true) {
			return null;	
		}
//		//cerco percorso ALTERNATIVA 1
//		System.out.println(componenteConnessa);
//		Airport current=d;
//		List<Airport> percorso= new ArrayList<>();
//		percorso.add(current);
//		DefaultWeightedEdge edge=iteratore.getSpanningTreeEdge(current);
//		while(edge!=null) {
//			current=Graphs.getOppositeVertex(this.grafo, edge, current);
//			percorso.add(0, current);
//			edge=iteratore.getSpanningTreeEdge(current);
//		}
//		if(percorso.size()==2) {
//			System.out.println(this.grafo.getEdge(p,d));
//		}
//		// ALTERNATIVA 2
//		Airport current2=d;
//		List<Airport> percorso2= new ArrayList<>();
//		percorso2.add(current2);
//		current2=iteratore.getParent(current2);
//		while(!current2.equals(p)) {
//			percorso2.add(0, current2);
//			current2=iteratore.getParent(current2);
//			
//		}
//		percorso2.add(0, current2);
//		if(percorso2.size()==2) {
//			System.out.println(this.grafo.getEdge(p,d));
//		}
//		
//		
//		
//		return percorso2;
//	}
		//alternativa, algoritmo di dijkstra
		
		DijkstraShortestPath<Airport,DefaultWeightedEdge> cammino=new DijkstraShortestPath(this.grafo);
		GraphPath <Airport,DefaultWeightedEdge> camminoMinimo=cammino.getPath(p, d); //nullo se il cammino non esiste
		return camminoMinimo.getVertexList();
	}
	
	
	
	
	
}



















