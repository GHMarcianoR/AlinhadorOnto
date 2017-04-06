/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ufjf;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.jena.ontology.OntClass;

import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.ontology.OntModelSpec;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntProperty;
import org.apache.jena.rdf.model.RDFNode;
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
      
    public IniClusters(String ont1, String ont2) throws IOException
    {   
       
        ontmodel =  ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM); 
        ontmodel2 = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM);
        
        ontmodel.read(ont1,null);
        ontmodel2.read(ont2,null);

        Globais.onto1 = ontmodel.getNsPrefixURI("");
        Globais.onto2 = ontmodel2.getNsPrefixURI("");

        col = inicializaTriplas(ontmodel);
        col2 = inicializaTriplas(ontmodel2);

        int t =col.size()+col2.size();
        System.out.println("Total de entidades "+ t);        
      
        sintatico(ontmodel, ontmodel2);
        
        BufferedWriter bw = new BufferedWriter(new FileWriter("listaEntidades.txt"));
        
        for(SchemaGraph s : col) 
            bw.write(s.getRoot().getNome()+"\n");
        for(SchemaGraph s : col2) 
            bw.write(s.getRoot().getNome()+"\n");
        bw.flush();
        bw.close();
    
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

    private Collection<SchemaGraph> inicializaTriplas(OntModel omodel) throws IOException 
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
                       ExtendedIterator<RDFNode> itLabel =  t.listLabels(null);

                        
                            if(t.getLocalName() != null)
                              {                               
                                scG.setRoot(t.getLocalName().toLowerCase(), t.getURI());
                                List<String> list = new ArrayList<>();

                                while(itLabel.hasNext())
                                {
                                   RDFNode rd = (RDFNode)itLabel.next();
                                   String s = rd.asLiteral().getString().replace(" ", "_").toLowerCase();
                                   if(!s.equals(t.getLocalName()))
                                        list.add(s);                                   
                                }
                                if(!list.isEmpty())
                                {
                                    scG.getRoot().setLabelsOnto(list);
                                }
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
                                      ExtendedIterator<RDFNode> itLabel2 = c.listLabels(null);
                                      List<String> list2 = new ArrayList<>();
                                      while(itLabel2.hasNext())
                                      {
                                            RDFNode rd = (RDFNode)itLabel2.next();
                                            String s = rd.asLiteral().getString().replace(" ", "_").toLowerCase();
                                            if(!s.equals(t.getLocalName()))
                                                 list2.add(s);                                   
                                       }
                                      Entidade temp = new Entidade();
                                      temp.setNome(c.getLocalName().toLowerCase());
                                      temp.setURI(c.getURI());
                                      temp.setLabelsOnto(list2);
                                      scG.add(temp);  
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

    private  void sintatico(OntModel omodel, OntModel omodel2) throws IOException
    {       
       System.out.println("Iniciando Processo de Alinhamento Sintatico");

       ExtendedIterator<OntClass> itr = omodel.listClasses();
       ExtendedIterator<OntClass> itr2 = omodel2.listClasses();
       HashMap<String,String> hmp1 = new HashMap<>();
       HashMap<String, String>hmp2 = new HashMap<>();
       BufferedWriter bw = new BufferedWriter(new FileWriter("Alinhamentos_Sintaticos.txt"));
      
        while(itr.hasNext())
       {
             OntClass t = (OntClass)itr.next();
             ExtendedIterator<RDFNode> itrd = t.listLabels(null);
             if(t.getLocalName() != null)
                 hmp1.put(t.getLocalName().toLowerCase(), t.getURI());
             while(itrd.hasNext())
             {
                 RDFNode rd = (RDFNode)itrd.next();
                 String s = rd.asLiteral().getString().replace(" ", "_").toLowerCase();
                 if(!s.equals(t.getLocalName().toLowerCase()))
                    hmp1.put(s, t.getURI());                
             }             
       }
       while(itr2.hasNext())
       {
            OntClass t = (OntClass)itr2.next();
            ExtendedIterator<RDFNode> itrd = t.listLabels(null);
            if(t.getLocalName() != null)
                hmp2.put(t.getLocalName().toLowerCase(), t.getURI());
             while(itrd.hasNext())
             {
                 RDFNode rd = (RDFNode)itrd.next();
                 String s = rd.asLiteral().getString().replace(" ", "_").toLowerCase();
                 if(!s.equals(t.getLocalName().toLowerCase()))
                       hmp2.put(s, t.getURI());                
             } 
       }      
       for(Map.Entry<String,String> ob : hmp1.entrySet())
            if(hmp2.containsKey(ob.getKey()))
            {
                Globais.resp.add(hmp2.get(ob.getKey())+";"+ob.getValue());
                bw.write(hmp2.get(ob.getKey())+";"+ob.getValue()+"\n");
            }
        
        System.out.println(Globais.resp.size()+" Sugestoes de entidades com alinhamento sintatico");
        bw.flush();
        bw.close();


        }
    
    public Collection<SchemaGraph> getSchem(){return col; }
    public Collection<SchemaGraph> getSchem2(){ return col2; }
 }
