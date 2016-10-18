package br.ufjf;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import uk.ac.shef.wit.simmetrics.similaritymetrics.Levenshtein;

public class IndirectMatching {
   
    private HashMap<Entidade, Entidade> mapeados;
    private Levenshtein lvMetric;
    
    
    public IndirectMatching(List<Cluster> list, HashMap<Cluster, List<Entidade>> anc) throws IOException
    {
        mapeados = new HashMap<>();
        lvMetric = new Levenshtein();
        match(list, anc);
        imprime();
        escreve_tudo(list);
        
    }
    private void escreve_tudo(List<Cluster> list) throws IOException
    {
        BufferedWriter bw = new BufferedWriter(new FileWriter("ALL.txt"));
        
        for(Cluster c : list)
        {
            bw.write("Cluster: "+c.getRoot().getNome()+"\n");
            if(c.getRoot().getRscLabel() != null)
                bw.write("\t\tLabel : "+c.getRoot().getRscLabel()+"\n");
            if(c.getRoot().getURI() != null)
                bw.write("\t\tURI"+c.getRoot().getURI()+"\n");
            if(c.getRoot().getPropriedades() != null &&!c.getRoot().getPropriedades().isEmpty())
                bw.write("\t\tPropriedades: "+c.getRoot().getPropriedades()+"\n" );
            if(c.getRoot().getListRedirects() != null && !c.getRoot().getListRedirects().isEmpty())
                bw.write("\t\tRedirects: "+c.getRoot().getListRedirects()+"\n");
            if(c.getRoot().getCategorias() != null && !c.getRoot().getCategorias().isEmpty())
                bw.write("\t\tCategories: "+c.getRoot().getCategorias()+"\n");
            bw.flush();
            for(Entidade e: c.getListEntidades() )
            {
                bw.write("\tEntidade: "+e.getNome()+"\n");
                if(e.getRscLabel() != null)
                    bw.write("\t\tLabel: "+e.getRscLabel()+"\n");
                if(e.getURI() != null)
                    bw.write("\t\tURI"+e.getURI()+"\n");
                if(e.getPropriedades() != null &&!e.getPropriedades().isEmpty())
                    bw.write("\t\tPropriedades: "+e.getPropriedades()+"\n" );
                if(e.getListRedirects() != null && !e.getListRedirects().isEmpty())
                    bw.write("\t\tRedirects: "+e.getListRedirects()+"\n");
                if(e.getCategorias() != null && !e.getCategorias().isEmpty())
                   bw.write("\t\tCategories: "+e.getCategorias()+"\n");
                bw.flush();
            }            
        }
        bw.close();
    }
    private void match(List<Cluster> listclusters, HashMap<Cluster, List<Entidade>> anc)
    {
        Iterator it = anc.entrySet().iterator();
        while(it.hasNext())
        {
            Map.Entry<Cluster, List<Entidade>> ob  = (Map.Entry<Cluster, List<Entidade>>)it.next();
            Cluster c  = listclusters.get(listclusters.indexOf(ob.getKey())); 
            
            for(Entidade e : ob.getValue())
            {                
                if(!e.getNome().equals(c.getRoot().getNome()))
                    if(verificaRedirects(e, c.getRoot()) 
                                    || verificaCategorias(e, c.getRoot()) 
                                    || verificaPropriedades(e, c.getRoot()))
                                mapeados.put(e, c.getRoot());
               
                for(Entidade e1 : c.getListEntidades())
                {
                    if(!e.getNome().equals(e1.getNome()))
                    {
                        if(verificaRedirects(e, e1) 
                                || verificaCategorias(e, e1) 
                                || verificaPropriedades(e, e1))
                            mapeados.put(e, e1);
                    }
                }
            }
        }
    
    }
    private void imprime() throws IOException
    {
         BufferedWriter bw = new BufferedWriter(new FileWriter("IndirectMatching.txt"));
         Iterator it =  mapeados.entrySet().iterator();
         bw.write("Total mapeados: "+mapeados.size()+'\n');
         while(it.hasNext())
         {
             Map.Entry<Entidade,Entidade> ob = (Map.Entry<Entidade,Entidade>)it.next();
             bw.write(ob.getKey().getURI()+" -> "+ob.getValue().getURI()+'\n');
             bw.flush();
         }
         bw.close();
    }
    public HashMap<Entidade,Entidade> getMapeados()
    {
        return mapeados;
    } 
    private boolean verificaRedirects(Entidade e, Entidade e1)
    {
        if(e.getListRedirects() != null)
        {
            for(String s : e.getListRedirects())
            {               
                if(lvMetric.getSimilarity(s, e1.getNome()) > 0.84)
                    return true;                
            }                
        }
        if(e1.getListRedirects() != null)
        {
            for(String s1 : e1.getListRedirects())
            {
               if(lvMetric.getSimilarity(s1, e.getNome()) > 0.84)
                            return true;
             }
        }        
     return false;
    }
    private boolean verificaPropriedades(Entidade e, Entidade e1)
    {
        int div = 0, divisor = 0;
        double result = 0;
        if(!e.getPropriedades().isEmpty() && !e1.getPropriedades().isEmpty())
        {
            for(String pro : e.getPropriedades())
                if(e1.getPropriedades().contains(pro))
                    div++;
            divisor = e.getPropriedades().size() + e1.getPropriedades().size();
            result = (double)div/divisor;
            return result > result;
        }
        return false;
    }
    private boolean verificaCategorias(Entidade e, Entidade e1)
    {        
        int div = 0, divisor = 0;
        double result = 0;
        if( (e.getCategorias() != null && !e.getCategorias().isEmpty()) &&
                (e1.getCategorias() != null && !e1.getCategorias().isEmpty()) )
        {
            for(String s : e.getCategorias())
                 if(e1.getCategorias().contains(s))
                     div++;
            
            divisor = e.getCategorias().size() + e1.getCategorias().size();
            result = (double)div/divisor;
            
            return result > 0.7;
        }
        
        return false;
    }
}
