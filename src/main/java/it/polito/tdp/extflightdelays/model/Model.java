package it.polito.tdp.extflightdelays.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
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
			
			if(grafo.vertexSet().contains(origin)&& grafo.vertexSet().contains(destination)){ 
				
				DefaultWeightedEdge edge=this.grafo.getEdge(origin, destination);// se non è ancora stato creato, sarà nullo!!
				
				if(edge!=null) { //l'arco esiste già ma sto considerando la coppia inversa
					double weight=this.grafo.getEdgeWeight(edge);
					weight += N;
					this.grafo.setEdgeWeight(origin, destination, weight);
					
				}else {
					this.grafo.addEdge(origin, destination);
					this.grafo.setEdgeWeight(origin, destination,N);
					}
			}
			
		}
		System.out.println("grafo creata");
		System.out.println("ci sono : "+this.grafo.vertexSet().size()+" vertici");
		System.out.println("ci sono : "+this.grafo.edgeSet().size()+" archi");
		
		
		return this.grafo.vertexSet();
	}
	
	public List<Airport> verificaConnessione(Airport p,Airport d) {
		if (this.grafo==null) {
			System.out.println("IL GRAFO E' VUOTO");
			return null;
		}
		if(this.grafo.getEdge(p, d)!=null) {
			System.out.println("AEREOPORTI DIRETTAMENTE COLLEGATI");
		}
		 BreadthFirstIterator<Airport, DefaultWeightedEdge> iteratore=new BreadthFirstIterator<>(this.grafo,p);
		 List<Airport> albero=new ArrayList<Airport>();
		 while(iteratore.hasNext()) {
			 albero.add(iteratore.next());	 
		 }
		 List<Airport> percorso=new ArrayList<Airport>();
		 if (albero.contains(d)) {
			 Airport attuale=d;
			 percorso.add(d);
			 DefaultWeightedEdge edge=iteratore.getSpanningTreeEdge(attuale);
			 while(edge!=null) {
				 attuale=Graphs.getOppositeVertex(this.grafo, edge, attuale);
				 percorso.add(attuale);
				 edge=iteratore.getSpanningTreeEdge(attuale);
			 }
			 return percorso;
		 }else {
			 System.out.println("I NODI NON SONO CONNESSI!!!!");
			 return null;
		 }
		
	}
	
	
	
}



















