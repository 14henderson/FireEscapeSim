package fireescapedemo;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;

public class PDFCreator{
    public static void createReport(simResults s){
        Document document = new Document();
        try
        {
            PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream("Simulation Results.pdf"));
            document.open();
            document.add(new Paragraph("Calculated simulation resluts: " +
                    "\nOriginal amount of souls in building: "+s.getSoulsStart() +
                    "\nEnd amound of souls in building: " +s.getSoulsEnd()+" ("+(s.getSoulsStart()-s.getSoulsEnd())+" souls escaped)" +
                    "\nTile taken: " + s.getTimeTaken()));
            document.close();
            writer.close();
        } catch (DocumentException e)
        {
            e.printStackTrace();
        } catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }
    }


    public static class simResults{
        private int soulsStart;
        private int soulsEnd = -1;
        private String timeTaken = "-1";
        public simResults(int soulsSt, int soulsEn, String timeTaken){
            this.soulsStart = soulsSt;
            this.soulsEnd = soulsEn;
            this.timeTaken = timeTaken;
        }
        public int getSoulsStart() {return soulsStart;}
        public void setSoulsStart(int soulsStart) {this.soulsStart = soulsStart;}
        public int getSoulsEnd() {return soulsEnd;}
        public void setSoulsEnd(int soulsEnd) {this.soulsEnd = soulsEnd;}
        public String getTimeTaken() {return timeTaken;}
        public void setTimeTaken(String timeTaken) {this.timeTaken = timeTaken;}
    }
}