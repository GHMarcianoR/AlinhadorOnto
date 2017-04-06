/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ufjf;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 *
 * @author zumbi
 */
public class Main {

    public static void main(String[] args) throws IOException {
        BufferedWriter bw;
        List<Cluster> listCluster;
        IndirectMatching indM;
        leParametros();
        try {
            bw = new BufferedWriter(new FileWriter("Log", true));

            bw.write("Inicio\n");
            bw.write(getDate() + " " + getTime() + "\n");
            bw.flush();

            Pre_processa pre_proc = new Pre_processa(args[0], args[1]);

            pre_proc.iniciaPreProcessamento();
            listCluster = pre_proc.getListCluster();
            
            if (Globais.Tipo_BK.equals("Especialista")) 
            {
                System.out.println("Alinhamento com apoio de BK Especialista Uberon");
                AncoragemEspecialista ancEsp = new AncoragemEspecialista();
                ancEsp.carregaDados();
                ancEsp.ancorar(listCluster);
                indM = new IndirectMatching(listCluster);
            } else if (Globais.Tipo_BK.equals("Generico")) 
            {
                System.out.println("Alinhamento com apoio de BK Generico DBPedia");
                AncoragemMemoria ancMem = new AncoragemMemoria(listCluster);
                ancMem.iniciarProcessoAncoragem();
            //    ancMem.dados();
                HashMap<Cluster, List<Entidade> > ancorados = ancMem.getAncorados();
                indM = new IndirectMatching(listCluster,  ancorados);
            } else if (Globais.Tipo_BK.equals("NoBK")) {
                System.out.println("Alinhamento sem apoio de BK");
            } else {
                System.err.print("Defina o tipo de Background Knowledge (Generico, Especialista, NoBK) no arquivo de parametros\n");
                System.exit(0);
            }
     
           

            cria_rdf();
            escrever_sugestao_alinhamento();
            bw.write("Termino\n");
            bw.write(getDate() + " " + getTime() + "\n");
            bw.flush();
            
        } catch (Exception e) {
            System.err.println("Erro: " + e.getMessage() + " " + Arrays.toString(e.getStackTrace()));
        }

    }

    public static String getTime() {
        DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
        Date date = new Date();
        return dateFormat.format(date);
    }
    public static String getDate() {
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        Date date = new Date();
        return dateFormat.format(date);
    }

    private static void leParametros() {

        try {
            BufferedReader br = new BufferedReader(new FileReader("parametros.txt"));
            List<String> l = new ArrayList<>();

            while (br.ready()) {
                l.add(br.readLine());
            }

            Globais.val_seleciona_candidatos = Double.parseDouble(l.get(0));
            Globais.val_redirects = Double.parseDouble(l.get(1));
            Globais.indirect_categories_prop = Double.parseDouble(l.get(2));
            Globais.path_labels = l.get(3).trim();
            Globais.path_redirects = l.get(4).trim();
            Globais.path_article_categories = l.get(5).trim();
            Globais.path_anchor_text = l.get(6).trim();
            Globais.path_ontoEspecialista1 = l.get(7).trim();
            Globais.path_ontoEspecialista2 = l.get(8).trim();
            Globais.Tipo_BK = l.get(9).trim();

        } catch (IOException | NumberFormatException ex) {

            System.err.println("Erro ao tentar ler o arquivo de parametros: " + ex.getMessage());
            System.exit(1);
        }

    }

    private static void cria_rdf() throws IOException {
        BufferedWriter bw = new BufferedWriter(new FileWriter("Aligment.rdf"));
        bw.write("<?xml version='1.0' encoding='utf-8'?>");

        bw.write("<rdf:RDF xmlns='http://knowledgeweb.semanticweb.org/heterogeneity/alignment'\n"
                + "	 xmlns:rdf='http://www.w3.org/1999/02/22-rdf-syntax-ns#' \n"
                + "	 xmlns:xsd='http://www.w3.org/2001/XMLSchema#' \n"
                + "	 alignmentSource=\"LargeOntologyMatcher\">\n");
        bw.write("<Alignment>\n");
        bw.write("\t<xml>yes</xml>\n");
        bw.write("\t<level>0</level>\n");
        bw.write("\t<type>11</type>\n");
        bw.write("\t<onto1>" + Globais.onto1 + "</onto1>\n");
        bw.write("\t<onto2>" + Globais.onto2 + "</onto2>\n");
        bw.write("\t<uri1>" + Globais.onto1 + "</uri1>\n");
        bw.write("\t<uri2>" + Globais.onto2 + "</uri2>\n");
        for (String s : Globais.resp) {
            String[] entidade = s.split(";");
            bw.write("\t<map>\n");
            bw.write("\t\t<Cell>\n");
            bw.write("\t\t\t<entity1 rdf:resource=\"" + entidade[0] + "\"/>\n");
            bw.write("\t\t\t<entity2 rdf:resource=\"" + entidade[1] + "\"/>\n");
            bw.write("\t\t\t<measure rdf:datype=\"http://www.w3.org/2001/XMLSchema#float\">1.0</measure>\n");
            bw.write("\t\t\t<relation>=</relation>\n");
            bw.write("\t\t</Cell>\n");
            bw.write("\t</map>\n");
        }
        bw.write("</Alignment>\n");
        bw.write("</rdf:RDF>");

        bw.flush();
        bw.close();
    }
    private static  void escrever_sugestao_alinhamento() throws IOException
    {
         BufferedWriter bw = new BufferedWriter(new FileWriter("Sugestoes_Alinhamentos.txt"));
        for(String s : Globais.resp)              
                   bw.write(s+"\n");           
         bw.close();
    }

}
