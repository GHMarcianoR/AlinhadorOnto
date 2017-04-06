/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ufjf;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.apache.jena.ontology.OntClass;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntModelSpec;
import org.apache.jena.ontology.OntProperty;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.util.iterator.ExtendedIterator;

/**
 *
 * @author zumbi
 */
public class AncoragemEspecialista {

    private OntModel ontmodel;
    private OntModel ontmodel2;
    private Set<OntProperty> setProp;
    private HashMap<String, String> hOnto;

    public AncoragemEspecialista() {
        ontmodel = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM);
        ontmodel2 = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM);

        ontmodel.read(Globais.path_ontoEspecialista1, null);
        ontmodel2.read(Globais.path_ontoEspecialista2, null);
        setProp = new HashSet<>();
        hOnto = new HashMap<>();

    }

    public void carregaDados() 
    {
        ExtendedIterator eIt = ontmodel.listClasses();

        ExtendedIterator eIt2 = ontmodel.listAllOntProperties();
        while (eIt2.hasNext()) {

            OntProperty op = (OntProperty) eIt2.next();
            if (op.getLocalName().contains("Synonym")) {
                setProp.add(op);
            }

        }

        while (eIt.hasNext()) 
        {
            OntClass t = (OntClass) eIt.next();

            if (t.getLocalName() != null) 
            {
                String uri = t.getURI().toLowerCase();
                hOnto.put(t.getLocalName().toLowerCase(), uri);

                for (RDFNode rdf : t.listLabels(null).toList()) {
                    hOnto.put(rdf.asLiteral().getString().toLowerCase().replace(" ", "_"), uri);
                }

                for (OntProperty p : setProp) 
                {                    
                    try {
                        hOnto.put(t.getPropertyValue(p).asLiteral().getString().toLowerCase().replace(" ", "_"), uri);
                    } catch (Exception e) {
                    }

                }
            }

        }

    }
    
    public void ancorar_(List<Entidade> lEnt)
    {
        for(Entidade e :lEnt)
        {
            
            String anc = hOnto.get(e.getNome());
            if(anc == null)
            {
                for(String s : e.getLabelsOnto() )
                {
                    anc = hOnto.get(s);
                    if(anc != null)
                    {
                        e.setUriAncEsp(anc);
                        break;
                    }
                }
            }
            else
                e.setUriAncEsp(anc);
                    
            
        }
    }
    public void ancorar(List<Cluster> lCluster)
    {
        for(Cluster c : lCluster)
        {
            String ancRoot = hOnto.get(c.getRoot().getNome().toLowerCase()); 
            if(ancRoot == null)
            {
                if(c.getRoot().getLabelsOnto()!= null)
                    for(String s : c.getRoot().getLabelsOnto())
                    {
                        ancRoot  = hOnto.get(s.toLowerCase());
                        if(ancRoot != null)
                        {
                            c.getRoot().setUriAncEsp(ancRoot);
                            break;
                        }
                    }
            }
            else
                c.getRoot().setUriAncEsp(ancRoot);
                

            for(Entidade e : c.getListEntidades())
            {
                String ancEnt = hOnto.get(e.getNome());
                if(ancEnt == null)
                {
                    if(e.getLabelsOnto() != null)
                        for(String s1 : e.getLabelsOnto())
                        {
                            ancEnt = hOnto.get(s1);
                            if(ancEnt != null)
                            {
                                e.setURI(ancEnt);
                                break;
                            }
                        }
                    
                }                  
                else
                    e.setUriAncEsp(ancEnt);
                                
            }
            
        }
        
    }
}
