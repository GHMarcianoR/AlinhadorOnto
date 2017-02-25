package br.ufjf;

import java.util.ArrayList;
import java.util.List;

public class SchemaGraph {
   private List<Entidade> nos;
   private String nomeOnto;
   private Entidade root;
   public SchemaGraph()
   {
     nos = new ArrayList<Entidade>();
     root = new Entidade();
   }
   public void setRoot(String n, String u)
   {
       root.setNome(n);
       root.setURI(u);
   }
      
   public void add(String n, String u)
   {
       Entidade temp = new Entidade();
       temp.setNome(n);
       temp.setURI(u);
       nos.add(temp);
   }
   public Entidade getEntidade(String n)
   {
       if(root.getNome().equals(n))
           return root;
       else
       {
           for(Entidade e : nos)
              if (e.getNome().equals(n))
                   return e;
       }
       return null;
   }
   public void add(Entidade e){nos.add(e);}
   public void add(List<Entidade> l){nos.addAll(l);}
   public List<Entidade> getListEntidade(){return nos;}
   public Entidade getRoot(){return root;}
   public void setNome(String n){ nomeOnto = n;}
   public String getNome(){return nomeOnto;}
     
}
