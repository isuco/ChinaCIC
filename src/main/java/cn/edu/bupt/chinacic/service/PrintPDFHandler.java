package cn.edu.bupt.chinacic.service;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;

import java.io.IOException;

public class PrintPDFHandler extends PdfPageEventHelper {
    private String expert;

    public PrintPDFHandler(String expert) {
        super();

        this.expert = expert;
    }

    @Override
    public void onEndPage(PdfWriter writer, Document document) {
        String sign = expert + " 专家签字 :  ____________";
        Paragraph signPara = new Paragraph(sign, FontFactory.getFont("STSong-Light","UniGB-UCS2-H", 15, Font.BOLD, BaseColor.BLACK));
        ColumnText.showTextAligned(writer.getDirectContent(), Element.ALIGN_CENTER, signPara,
                document.right() / 2 , document.bottom(), 0);
    }


//    @Override
//    public void onCloseDocument(PdfWriter writer, Document document) {
//        ColumnText.showTextAligned(template,Element.PARAGRAPH,new Phrase(String.valueOf(writer.getPageNumber()-1)),2,2,0);
//    }
}
