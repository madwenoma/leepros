package action;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2017/12/1.
 */
public class RenamePDF {
    public static void main(String[] args) throws IOException {
        List<String> lines = FileUtils.readLines(new File("c:\\1.txt"), "utf-8");
        Map<String, String> nameRelations = new HashMap<String, String>();
        for (String line : lines) {
            String[] nameFile = line.split("#");
            nameRelations.put(nameFile[1], nameFile[0].trim());
        }

        Collection<File> pdfFiles = FileUtils.listFiles(new File("d:\\books"), new String[]{"pdf"}, false);
        for (File pdf : pdfFiles) {
//            String fileName = StringUtils.substringAfterLast(pdf.getName(), "/");
            String toFileName = nameRelations.get(pdf.getName());
            if (StringUtils.isNotEmpty(toFileName)) {

                pdf.renameTo(new File(pdf.getParent() + "\\" + toFileName + ".pdf"));
            }
        }
    }
}
