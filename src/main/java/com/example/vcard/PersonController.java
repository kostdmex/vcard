package com.example.vcard;

import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@Controller
public class PersonController {
	
    @RequestMapping(value = "/", method = RequestMethod.POST)
    public String search(@RequestParam String name, final Model model) throws IOException {

        List<Person> persons = new LinkedList<>();
        
        String url = "https://adm.edu.p.lodz.pl/user/users.php?search=" + name;
        Document doc = Jsoup.connect(url).get();
        Elements profiles = doc.select(".user-profile");
        for (Element profile : profiles) {
            Elements username = profile.select(".user-info").select("h3");
            Elements faculty = profile.select(".user-info").select(".extra-info").select(".item-content");
            persons.add(new Person(username.text(), faculty.text()));
        }
        model.addAttribute("persons", persons);

        return "search";
    }

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String search(final Model model) throws IOException {
        return "search";
    }

    @RequestMapping(value = "/vcard", method = RequestMethod.GET)
    public File generateVCard(HttpSession session, HttpServletResponse response,
    		@RequestParam String name, @RequestParam String faculty) throws IOException {
        File fileToDownload = getFileVcard(name, faculty);
        InputStream inputStream = new FileInputStream(fileToDownload);
        response.setContentType("application/force-download");
        response.setHeader("Content-Disposition", "inline;filename=vcard.vcf");
        response.setHeader("Content-Type", "text/calendar; charset=utf-8");
        IOUtils.copy(inputStream, response.getOutputStream());
        response.flushBuffer();
        inputStream.close();
        return fileToDownload;
    }
    
    private File getFileVcard(String name, String faculty) {
    	File file = new File("vcard.vcs");
        try {
            file.createNewFile();
            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file));
            bufferedWriter.write("BEGIN:VCARD\n" +
                    "VERSION:3.0\n" +
                    "N:" + name + ";\n" +
                    "ADR;TYPE=WORK,PREF:;;" + faculty + "\n" +
                    "END:VCARD");
            bufferedWriter.close();
            return file;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}