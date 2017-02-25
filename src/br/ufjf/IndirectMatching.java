package br.ufjf;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class IndirectMatching {
   
    private HashMap<Entidade, Entidade> mapeados;
    private int qtd_mapeados;
    
    public IndirectMatching(List<Cluster> list, HashMap<Cluster, List<Entidade>> anc) throws IOException
    {
        System.out.println("Iniciando IndirectMatching");
        mapeados = new HashMap<>();
        qtd_mapeados = 0;
        match(list, anc);
        imprime();

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
                 if(!e.equals(c.getRoot()) && (!e.getURI().contains("bioontology.org") || !c.getRoot().getURI().contains("bioontology.org"))
                                     && (!e.getURI().contains("ncicb.nci") || !c.getRoot().getURI().contains("ncicb.nci")) )
                    if(verificaRedirects(e, c.getRoot())  || verificaCategorias(e, c.getRoot()) || verificaPropriedades(e, c.getRoot()) )
                    { 
                        if(!Globais.resp.contains(e.getURI()+";"+c.getRoot().getURI()) && !Globais.resp.contains(c.getRoot().getURI()+";"+e.getURI()) )
                        {
                            Globais.resp.add(e.getURI()+";"+c.getRoot().getURI());
                            qtd_mapeados++;
                        }
                    }
               
                for(Entidade e1 : c.getListEntidades())
                {
                    if(!e.equals(e1) && (!e.getURI().contains("bioontology.org") || !e1.getURI().contains("bioontology.org"))
                                     && (!e.getURI().contains("ncicb.nci") || !e1.getURI().contains("ncicb.nci")) )
                        if(verificaRedirects(e, e1)  || verificaCategorias(e, e1) || verificaPropriedades(e, e1))
                        {
                            if(!Globais.resp.contains(e.getURI()+";"+e1.getURI()) && !Globais.resp.contains(e1.getURI()+";"+e.getURI()))
                            {
                                Globais.resp.add(e.getURI()+";"+e1.getURI()); 
                                qtd_mapeados++;
                            
                            }
                        }
                }
            }
        
        }System.out.println("Quantidade mapeados Indirect: "+qtd_mapeados);
    
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
                
                if(s.contains(e1.getNome()) || e1.getNome().contains(s))
                    return true;
         
            }                
        }
        if(e1.getListRedirects() != null)
        {
            for(String s1 : e1.getListRedirects())
            {
               
                if(s1.contains(e.getNome()) || e.getNome().contains(s1))
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
            return result > Globais.indirect_categories_prop;
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
            
            return result > Globais.indirect_categories_prop;
        }
        
        return false;
    }

    private void imprime() throws IOException
    {
         BufferedWriter bw = new BufferedWriter(new FileWriter("Alinhamentos.txt"));
        for(String s : Globais.resp)              
                   bw.write(s+"\n");
           
         bw.close();
    }
}
