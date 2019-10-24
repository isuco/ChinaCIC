package cn.edu.bupt.chinacic.service;

import cn.edu.bupt.chinacic.pojo.po.Expert;
import cn.edu.bupt.chinacic.pojo.po.ExpertProject;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.ColumnText;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
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

    private PdfPCell newCell(String content, Font font, int colspan){
        PdfPCell retCell = new PdfPCell(new Paragraph(content, font));
        retCell.setColspan(colspan);
        centerAlignment(retCell);

        return retCell;
    }

    public void printVotePerExpert(List<ExpertProject> votesOfExpert, Expert expert, String filePath) {
        Document document;
        try {
            document = openDocument(filePath, expert.getName());
        } catch (IOException | DocumentException e) {
            log.error("{}专家投票结果文件创建失败", expert.getName());
            return;
        }

        // 标题
        Calendar calendar = Calendar.getInstance();
        String title = calendar.get(Calendar.YEAR) + "年度中国通信学会科学技术奖评审委员会选票";
        Paragraph titlePara = new Paragraph(title, FontFactory.getFont("STSong-Light","UniGB-UCS2-H", 20, Font.BOLD, BaseColor.BLACK));
        titlePara.setAlignment(Element.ALIGN_CENTER);
        titlePara.setSpacingAfter(15);

        // 表格
        PdfPTable table = new PdfPTable(27);
        Font cellFont = FontFactory.getFont("STSong-Light", "UniGB-UCS2-H", 12);
        Font cellTitleFont = FontFactory.getFont("STSong-Light", "UniGB-UCS2-H", 12, Font.BOLD);

        table.addCell(newCell("编号", cellTitleFont, 2));
        table.addCell(newCell("特等奖", cellTitleFont, 1));
        table.addCell(newCell("一等奖", cellTitleFont, 1));
        table.addCell(newCell("二等奖", cellTitleFont, 1));
        table.addCell(newCell("三等奖", cellTitleFont, 1));
        table.addCell(newCell("项目名称", cellTitleFont, 8));
        table.addCell(newCell("主要完成单位", cellTitleFont, 6));
        table.addCell(newCell("项目类别", cellTitleFont, 4));
        table.addCell(newCell("获奖结果", cellTitleFont, 3));

        for (ExpertProject votePerExpert : votesOfExpert) {
            // 编号
            table.addCell(newCell(votePerExpert.getProject().getNumber(), cellFont, 2));
            // 特等奖
            table.addCell(transIntToChar(votePerExpert.getSpecialNum(), "√", "×", cellFont, 1));
            // 一等奖
            table.addCell(transIntToChar(votePerExpert.getFirstNum(), "√", "×", cellFont, 1));
            // 二等奖
            table.addCell(transIntToChar(votePerExpert.getSecondNum(), "√", "×", cellFont, 1));
            // 三等奖
            table.addCell(transIntToChar(votePerExpert.getThirdNum(), "√", "×", cellFont, 1));
            // 项目名称
            String name = votePerExpert.getProject().getNumber() + " " + votePerExpert.getProject().getName();
            table.addCell(newCell(name, cellFont, 8));
            // 主要完成单位
            table.addCell(newCell(votePerExpert.getProject().getMainCompUnit(), cellFont, 6));
            // 项目类别
            table.addCell(newCell(votePerExpert.getProject().getRecoUnit(), cellFont, 4));
            // 获奖结果
            table.addCell(newCell(votePerExpert.getProject().getPrize(), cellFont, 3));
//            numCell.setMinimumHeight(20);
        }
        table.setHeaderRows(1);

        // 签字
//        String sign = expert.getName() + " 专家签字 :  ____________";
//        Paragraph signPara = new Paragraph(sign, FontFactory.getFont("STSong-Light","UniGB-UCS2-H", 12, Font.BOLD, BaseColor.BLACK));
//        signPara.setAlignment(Element.ALIGN_CENTER);
//        signPara.setSpacingBefore(10);

        try {
            // 添加表头
            document.add(titlePara);
            // 添加表格
            document.add(table);

            // 添加签字区
//            document.add(signPara);

        } catch (DocumentException e) {
            log.error("{}专家表格生成失败", expert.getName());
        }
        log.info("{}专家投票文件生成成功，文件位置：{}", expert.getName(), filePath);

//        document.setMargins(5,5,10,10);
        document.close();
    }

    private void centerAlignment(PdfPCell cell) {
        cell.setUseAscender(true);
        cell.setUseDescender(true);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
    }

    private PdfPCell transIntToChar(int vote, String right, String wrong, Font cellFont, int colspan) {
        PdfPCell pdfPCell;
        if (vote != 0) pdfPCell = newCell(right, cellFont, colspan);
        else pdfPCell = newCell(wrong, cellFont, colspan);

        return pdfPCell;
    }

    private Document openDocument(String filePath, String expertName) throws FileNotFoundException, DocumentException {
        Document document = new Document(PageSize.A4, 5,5,15,15);
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
        PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(pdfFile));
        writer.setPageEvent(new PrintPDFHandler(expertName));
        document.open();
        return document;
    }

}
