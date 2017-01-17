/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ufjf;

import java.util.ArrayList;
import java.util.Collection;
import org.apache.jena.ontology.OntClass;

import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.ontology.OntModelSpec;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntProperty;
import org.apache.jena.util.iterator.ExtendedIterator;

/**
 *
 * @author zumbi
 */
public class IniClusters {
    
    private Collection<SchemaGraph> col;
    private Collection<SchemaGraph> col2;
    private final OntModel ontmodel;
    private final OntModel ontmodel2;  
      
    public IniClusters(String ont1, String ont2)
    {   
       
        ontmodel =  ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM); 
        ontmodel2 = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM);
        ontmodel.read(ont1,null);
        ontmodel2.read(ont2,null);
        col = inicializaTriplas(ontmodel);
        col2 = inicializaTriplas(ontmodel2);
        int total = col.size()+col2.size();
        System.out.println("Total de entidades "+total);        
    }
    
    private void carregaPropriedades(OntClass t, SchemaGraph s)
    {
        for (OntProperty propriedades : t.listDeclaredProperties(false).toList())
        {   try
            {
                s.getEntidade(t.getLocalName()).addPropriedades(propriedades.getURI(), propriedades.getLocalName());
            }catch(NullPointerException e){}
           
        }
        
    }

    private Collection<SchemaGraph> inicializaTriplas(OntModel omodel) 
    {
                Collection<SchemaGraph>  cSg = new ArrayList<>();
                SchemaGraph scG;
                System.out.println("Inicializando...");
                ExtendedIterator<OntClass> itr = omodel.listClasses();
                String nome = getNome(omodel);
             
                while(itr.hasNext())
                {                    
                      scG = new SchemaGraph();
                      scG.setNome(nome);
                      OntClass t = (OntClass)itr.next();
                      
                            if(t.getLocalName() != null)
                              {                               
                                scG.setRoot(t.getLocalName(), t.getURI());
                                if(!t.listDeclaredProperties(false).toList().isEmpty())
                                 {
                                     carregaPropriedades(t,scG);
                                 }  
                                if(!t.listSubClasses().toList().isEmpty())
                                {
                                    ExtendedIterator i = t.listSubClasses();
                                    while(i.hasNext())
                                    {
                                      OntClass c = (OntClass)i.next();   
                                      scG.addNo(c.getLocalName(),c.getURI());  
                                      carregaPropriedades(c,scG);                                       
                                    }                                 
                                }                              
                               
                               cSg.add(scG);
                            }                      
                     
                }
                return cSg;
    }    
    private String getNome(OntModel omodel)
    {
        String nome = null;
        ExtendedIterator<OntClass> itr = omodel.listClasses();
        while(itr.hasNext())
        {
            OntClass t = (OntClass)itr.next();
            if(t.getURI() != null && t.getURI().contains("#"))
            {
                nome = t.getURI().substring(0, t.getURI().indexOf("#"));
                break;
            }
        }
        return nome;
    }
    public Collection<SchemaGraph> getSchem(){return col; }
    public Collection<SchemaGraph> getSchem2(){ return col2; }
 }
