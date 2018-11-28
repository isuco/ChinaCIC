package cn.edu.bupt.chinacic.service;

import cn.edu.bupt.chinacic.pojo.po.Expert;
import cn.edu.bupt.chinacic.pojo.po.ExpertProject;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.List;

@Service
@Slf4j
public class PrintService {

    private Calendar calendar;

    @PostConstruct
    public void init() {
        calendar = Calendar.getInstance();
    }

    public void printVotePerExpert(List<ExpertProject> votesOfExpert, Expert expert, String filePath) {
        Document document;
        try {
            document = openDocument(filePath);
        } catch (IOException | DocumentException e) {
            log.error("{}专家投票结果文件创建失败");
            return;
        }

        Font cellFont = FontFactory.getFont("STSong-Light", "UniGB-UCS2-H", 12);
        PdfPTable table = new PdfPTable(11);
        Font titleFont = FontFactory.getFont("STSong-Light", "UniGB-UCS2-H", 12, Font.BOLD);
        PdfPCell titleCell = new PdfPCell(new Paragraph(calendar.get(Calendar.YEAR) + "年度中国通信学会科学技术奖评审委员会选票", titleFont));
        titleCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        titleCell.setColspan(11);
        titleCell.setMinimumHeight(20);
        table.addCell(titleCell);

        PdfPCell numTitleCell = new PdfPCell(new Paragraph("编号", cellFont));
        PdfPCell specialTitleCell = new PdfPCell(new Paragraph("特等奖", cellFont));
        PdfPCell firstTitleCell = new PdfPCell(new Paragraph("一等奖", cellFont));
        PdfPCell secondTitleCell = new PdfPCell(new Paragraph("二等经", cellFont));
        PdfPCell thirdTitleCell = new PdfPCell(new Paragraph("三等奖", cellFont));
        PdfPCell nameTitleCell = new PdfPCell(new Paragraph("项目名称", cellFont));
        nameTitleCell.setColspan(3);
        PdfPCell recUnitTitleCell = new PdfPCell(new Paragraph("提名单位", cellFont));
        recUnitTitleCell.setColspan(3);

        centerAlignment(numTitleCell);
        table.addCell(numTitleCell);
        centerAlignment(specialTitleCell);
        table.addCell(specialTitleCell);
        centerAlignment(firstTitleCell);
        table.addCell(firstTitleCell);
        centerAlignment(secondTitleCell);
        table.addCell(secondTitleCell);
        centerAlignment(thirdTitleCell);
        table.addCell(thirdTitleCell);
        centerAlignment(nameTitleCell);
        table.addCell(nameTitleCell);
        centerAlignment(recUnitTitleCell);
        table.addCell(recUnitTitleCell);

        for (ExpertProject votePerExpert : votesOfExpert) {
            PdfPCell numCell = new PdfPCell(new Paragraph(votePerExpert.getProject().getNumber(), cellFont));
            numCell.setMinimumHeight(20);
            centerAlignment(numCell);
            PdfPCell specialCell = transIntToChar(votePerExpert.getSpecialNum(), "√", "×", cellFont);
            specialCell.setMinimumHeight(20);
            centerAlignment(specialCell);
            PdfPCell firstCell = transIntToChar(votePerExpert.getFirstNum(), "√", "×", cellFont);
            firstCell.setMinimumHeight(20);
            centerAlignment(firstCell);
            PdfPCell secondCell = transIntToChar(votePerExpert.getSecondNum(), "√", "×", cellFont);
            secondCell.setMinimumHeight(20);
            centerAlignment(secondCell);
            PdfPCell thirdCell = transIntToChar(votePerExpert.getThirdNum(), "√", "×", cellFont);
            thirdCell.setMinimumHeight(20);
            centerAlignment(thirdCell);
            PdfPCell nameCell = new PdfPCell(new Paragraph(votePerExpert.getProject().getNumber() + " "
                    + votePerExpert.getProject().getName(), cellFont));
            nameCell.setMinimumHeight(20);
            nameCell.setColspan(3);
            centerAlignment(nameCell);
            PdfPCell recUnitCell = new PdfPCell(new Paragraph(votePerExpert.getProject().getRecoUnit(), cellFont));
            recUnitCell.setMinimumHeight(20);
            recUnitCell.setColspan(3);
            centerAlignment(recUnitCell);
            table.addCell(numCell);
            table.addCell(specialCell);
            table.addCell(firstCell);
            table.addCell(secondCell);
            table.addCell(thirdCell);
            table.addCell(nameCell);
            table.addCell(recUnitCell);
        }
        try {
            document.add(table);
        } catch (DocumentException e) {
            log.error("{}专家表格生成失败", expert.getName());
        }
        document.close();
    }

    private void centerAlignment(PdfPCell cell) {
        cell.setUseAscender(true);
        cell.setUseDescender(true);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
    }

    private PdfPCell transIntToChar(int vote, String right, String wrong, Font cellFont) {
        PdfPCell pdfPCell;
        if (vote != 0) pdfPCell = new PdfPCell(new Paragraph(right, cellFont));
        else pdfPCell = new PdfPCell(new Paragraph(wrong, cellFont));
        return pdfPCell;
    }

    private Document openDocument(String filePath) throws FileNotFoundException, DocumentException {
        Document document = new Document();
        boolean pdfSuffix = filePath.endsWith(".pdf");
        if (pdfSuffix) {
            filePath = filePath.substring(0, filePath.length() - 4);
        }
        File pdfFile = new File(filePath + ".pdf");
        int index = 0;
        while (pdfFile.exists()) {
            pdfFile = new File(filePath + index + ".pdf");
        }
        File pdfDir = pdfFile.getParentFile();
        if (!pdfDir.exists()) pdfDir.mkdirs();
        PdfWriter.getInstance(document, new FileOutputStream(pdfFile));
        document.open();
        return document;
    }

}
