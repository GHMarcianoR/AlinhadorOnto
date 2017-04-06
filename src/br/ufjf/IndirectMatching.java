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
    private BufferedWriter bw;
    
    public IndirectMatching(List<Cluster> list, HashMap<Cluster, List<Entidade>> anc) throws IOException
    {
        System.out.println("Iniciando IndirectMatching");
        mapeados = new HashMap<>();
        qtd_mapeados = 0;
        bw = new BufferedWriter(new FileWriter ("IndirectMatching"));
        match(list, anc);
        System.out.println("MAPEADOS: "+qtd_mapeados);
        bw.flush();
        bw.close();
    }
    public IndirectMatching (List<Cluster> list) throws IOException
    {
         System.out.println("Iniciando IndirectMatching");
        mapeados = new HashMap<>();
        qtd_mapeados = 0;
        bw = new BufferedWriter(new FileWriter ("IndirectMatching"));
        match(list);
        System.out.println("MAPEADOS: "+qtd_mapeados);
        bw.flush();
        bw.close();
    }

    private void match(List<Cluster> listclusters, HashMap<Cluster, List<Entidade>> anc) throws IOException
    {
        Iterator it = anc.entrySet().iterator();
        while(it.hasNext())
        {
            Map.Entry<Cluster, List<Entidade>> ob  = (Map.Entry<Cluster, List<Entidade>>)it.next();
            Cluster c  = listclusters.get(listclusters.indexOf(ob.getKey())); 
            
            for(Entidade e : ob.getValue())
            {                
                 if(verificaOntoDiferentes(e, c.getRoot()))
                    if(verificaRedirects(e, c.getRoot())  || verificaCategorias(e, c.getRoot()) || verificaAnchor(e, c.getRoot()) )
                             verificaPossivelMatch(e, c.getRoot());
                 
                for(Entidade e1 : c.getListEntidades())
                {
                   if(verificaOntoDiferentes(e, e1))
                        if(verificaRedirects(e, e1)  || verificaCategorias(e, e1) || verificaAnchor(e, e1))
                                    verificaPossivelMatch(e, e1);
                        
                }
            }
        
        }
    
    }
    
    private void match(List<Cluster> listclusClusters) throws IOException
    {
        for(Cluster c : listclusClusters)
        {
            String uri1, uri2,a;
            uri1 = c.getRoot().getUriAncEsp();
            for(Entidade e : c.getListEntidades())
            {
                uri2 = e.getUriAncEsp();
                if(verificaOntoDiferentes(e, c.getRoot()))
                    if(uri1 != null && uri2 != null && uri1.equals(uri2))
                        verificaPossivelMatch(e, c.getRoot());
                 //  a ="";
            }
           
            for(Entidade e : c.getListEntidades())
            {
               uri1 = e.getUriAncEsp();
               for(Entidade e1 : c.getListEntidades())
               {
                   uri2 = e1.getUriAncEsp();
                   if(verificaOntoDiferentes(e, e1))
                    if(uri1 != null && uri2 != null && uri1.equals(uri2))
                      verificaPossivelMatch(e,e1);
                   //    a ="";
               }
            }
            
            
        }
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
    private boolean verificaAnchor(Entidade e, Entidade e1)
    {
        if(e.getLabelsOnto() != null && e1.getAnchorText() != null)
            for(String s : e.getLabelsOnto())
                if(e1.getAnchorText().equals(s))
                return true;
        if(e1.getLabelsOnto() != null && e.getAnchorText() != null)
            for(String s : e1.getLabelsOnto())
                if(e.getAnchorText().equals(s))
                return true;
        if(e.getListRedirects() != null && e1.getListRedirects() != null
                && e.getAnchorText() != null && e1.getAnchorText() != null)
            if(e.getListRedirects().contains(e1.getAnchorText()) || e1.getListRedirects().contains(e.getAnchorText()))
                return true;
                
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
    private void verificaPossivelMatch(Entidade e, Entidade e1) throws IOException
    {
        
        if(!Globais.resp.contains(e.getURI()+";"+e1.getURI()) && !Globais.resp.contains(e1.getURI()+";"+e.getURI()))
        {
            Globais.resp.add(e.getURI()+";"+e1.getURI()); 
            qtd_mapeados++;
            bw.write(e.getURI()+";"+e1.getURI()+"\n");                    
                            
        }
      
    }
    private boolean verificaOntoDiferentes(Entidade e, Entidade e1)
    {
        return !e.equals(e1) && (!e.getURI().contains("bioontology.org") || !e1.getURI().contains("bioontology.org"))
                && (!e.getURI().contains("ncicb.nci") || !e1.getURI().contains("ncicb.nci"));
    }
    
}
