/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package com.mycompany.svgtohtml;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
/**
 *
 * @author Arthur
 */
public class MainClass {

    /**
     * @param args the command line arguments
     */
   public static void main(String[] args) throws IOException {

        Path path = Paths.get("input.txt");
        List<String> list = Files.readAllLines(path, StandardCharsets.UTF_8);
        Pattern characterAllowed = Pattern.compile("p\\d : *(C|F|\\[|\\]\\+|-|) *-> *([C|F|\\+|\\-|\\[|\\]]+)");
        int numberOfInterations = 0;
        String axion = "";
        int degrees = 0;
        Map<String, String> rules = new HashMap<String, String>();
        
        for (String line : list) {
            if (line != null) {
                Matcher matcher = characterAllowed.matcher(line);
                String firstWord = line.split(":")[0].strip();
                String secondWord = line.split(":")[1].strip();
                
                if ("n".equals(firstWord)) {
                    numberOfInterations = Integer.parseInt(secondWord);
                } else if ("St".equals(firstWord)) {
                    axion = secondWord;
                } else if ("Dg".equals(firstWord)) {
                    degrees = Integer.parseInt(secondWord);
                } else if (matcher.find()) {
                    rules.put(matcher.group(1), matcher.group(2));
                }
            }
        }
        
        StringBuilder stringToBuildHTML = new StringBuilder();
        stringToBuildHTML.append(axion);
        System.out.println("n = 0: " + axion);
        
        for (int i = 1; i <= numberOfInterations; i++) {
            String currentString = stringToBuildHTML.toString();
            stringToBuildHTML.setLength(0);
            
            for (char letter : currentString.toCharArray()) {
                if (!rules.containsKey(String.valueOf(letter))) {
                    stringToBuildHTML.append(String.valueOf(letter));
                } else {
                    stringToBuildHTML.append(rules.get(String.valueOf(letter)));
                }
            }
             System.out.println(stringToBuildHTML.toString());
        }
        List<Double> x = new ArrayList<>();
        List<Double> y = new ArrayList<>();
        List<Double> angulo = new ArrayList<>();
        
        x.add((double) 40);
        y.add((double) 50);
        angulo.add(Math.toRadians(-60));
        double step = 0.5;
        x.add(x.get(x.size() - 1) + (step * Math.cos(angulo.get(angulo.size() - 1))));
        y.add(y.get(y.size() - 1) + (step * Math.sin(angulo.get(angulo.size() - 1))));

        List<String> linhas = new ArrayList<>();
        for(char c : stringToBuildHTML.toString().toCharArray()){
            if(c == 'C' || c == 'F'){
                StringBuilder linha = new StringBuilder();
                linha.append("<line x1=\"");
                linha.append(x.get(x.size()-2));
                linha.append("%\" y1=\"");
                linha.append(y.get(y.size()-2));

                linha.append("%\" x2=\"");
                linha.append(x.get(x.size()-1));
                linha.append("%\" y2=\"");
                linha.append(y.get(y.size()-1));
                linha.append("%\"/>");
                linhas.add(linha.toString());
            }
            if(c == 'C' || c == 'F'){
                x.remove(x.size()-2);
                y.remove(y.size()-2);
                x.add(x.get(x.size() - 1) + (step * Math.cos(angulo.get(angulo.size() - 1))));
                y.add(y.get(y.size() - 1) + (step * Math.sin(angulo.get(angulo.size() - 1))));
            }
            else if(c == '+' || c == '-'){
                if(c == '+')
                    angulo.add(angulo.get(angulo.size() - 1) - Math.toRadians(degrees));
                    
                else if(c == '-')
                    angulo.add(angulo.get(angulo.size() - 1) + Math.toRadians(degrees));

                angulo.remove(angulo.size() - 2);
                
                x.remove(x.size()-1);
                y.remove(y.size()-1);

                x.add(x.get(x.size() - 1) + (step * Math.cos(angulo.get(angulo.size() - 1))));
                y.add(y.get(y.size() - 1) + (step * Math.sin(angulo.get(angulo.size() - 1))));
            }
            else if(c == '[' || c == ']'){
                if(c == '['){
                    x.add(x.get(x.size() - 2));
                    x.add(x.get(x.size() - 2));
                    y.add(y.get(y.size() - 2));
                    y.add(y.get(y.size() - 2));
                    angulo.add(angulo.get(angulo.size() - 1));
                }

                if(c == ']'){
                    x.remove(x.size() - 1);
                    x.remove(x.size() - 1);
                    y.remove(y.size() - 1);
                    y.remove(y.size() - 1);
                    angulo.remove(angulo.size() - 1);
                }
            }
        }
        Path finalPath = Paths.get("svg.html");
        StringBuilder build = new StringBuilder();
        build.append("<html><body style=\"background-color:purple;\"><div style=\"position: fixed; top: 0; z-index: 1000;\"></div><svg id=\"svgZoom\" viewBox=\"0 0 3000 3000\" preserveAspectRatio=\"xMidYMid meet\" style=\"stroke:rgb(4, 205, 255);stroke-width:2\">");
        for(String s : linhas){
            build.append(s);
        }
        build.append("</svg></body><script>const slider = document.getElementById(\"zoomRange\");const svgZoom = document.getElementById(\"svgZoom\");const zoomValue = document.getElementById(\"zoomValue\");slider.oninput = function() {zoomValue.innerText = `${this.value}%`;svgZoom.style.transform = `scale(${this.value / 100})`;}</script></html>");
        byte[] bytes = build.toString().getBytes();

        Files.write(finalPath, bytes);
    }
    
}
