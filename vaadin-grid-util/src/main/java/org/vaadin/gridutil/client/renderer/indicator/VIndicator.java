package org.vaadin.gridutil.client.renderer.indicator;

import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.user.client.ui.HTML;

public class VIndicator extends HTML {

    private String colorStyle;
    private Double data;

    private double startGreen = -1, startRed = -1;

    private NumberFormat fmt;

    public VIndicator() {
        addMouseOutHandler(new MouseOutHandler() {

            @Override
            public void onMouseOut(MouseOutEvent event) {
                drawArrow();
            }
        });
        addMouseOverHandler(new MouseOverHandler() {

            @Override
            public void onMouseOver(MouseOverEvent event) {
                drawValue();
            }
        });
        setStylePrimaryName("v-grid-cell-indicator");
        fmt = NumberFormat.getDecimalFormat();
    }

    public void setValue(Double data, Double startGreen, Double startRed) {
        this.data = data;
        this.startGreen = startGreen;
        this.startRed = startRed;
        this.colorStyle = getColorStyleName();
        drawArrow();
    }

    private void drawValue() {
        if (data == null) {
            setHTML("null");
        }
        else {
            setHTML("<span class=\"" + colorStyle + "\">" + getArrowCode() + " " + fmt.format(this.data) + "</span>");
        }
    }

    private String getArrowCode() {
        if (data == null || startGreen == -1 || startRed == -1) {
            return "n/a";
        }
        else {
            if (data.doubleValue() > startGreen) {
                return "&#65514;";
            }
            else if (data.doubleValue() < startRed) {
                return "&#65516;";
            }
            else {
                return "&#61;";
            }
        }
    }

    private String getColorStyleName() {
        if (data == null || startGreen == -1 || startRed == -1) {
            return "na";
        }
        else {
            if (data.doubleValue() > startGreen) {
                return "green";
            }
            else if (data.doubleValue() < startRed) {
                return "red";
            }
            else {
                return "black";
            }
        }
    }

    private void drawArrow() {
        setHTML("<span class=\"" + colorStyle + "\">" + getArrowCode() + "</span>");
    }

}
