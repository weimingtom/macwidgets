package com.explodingpixels.widgets.plaf;

import com.explodingpixels.painter.Painter;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.plaf.basic.BasicTabbedPaneUI;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Paint;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseMotionListener;

public class EPTabbedPaneUI extends BasicTabbedPaneUI {

    private static final Color SELECTED_BORDER_COLOR = new Color(0x666666);
    private static final Color UNSELECTED_BORDER_COLOR = new Color(0x888888);
    private static final Color SELECTED_BACKGROUND_COLOR = Color.WHITE;
    private static final Color UNSELECTED_BACKGROUND_COLOR = new Color(0xcccccc);

    private static final int CORNER_ARC_DIAMETER = 6;

    private Painter<Component> fContentBorderTopEdgeBackgroundPainter = createContentBorderTopEdgeBackgroundPainter();
    private boolean fPaintFullContentBorder = true;
    private int fCurrentTabWidth = DEFAULT_TAB_WIDTH;

    private static final Insets FULL_CONTENT_BORDER_INSETS = new Insets(6, 0, 0, 0);
    private static final Insets HAIRLINE_BORDER_INSETS = new Insets(1, 0, 0, 0);
    private static final Insets DEFALT_TAB_INSETS = new Insets(2, 10, 2, 10);

    private static final int DEFAULT_TAB_WIDTH = 100;
    private static final int MAX_TAB_WIDTH = 55;

    private static final int CLOSE_BUTTON_LEFT_PAD = 6;
    private static final int CLOSE_BUTTON_RIGHT_PAD = 4;
    private static final ImageIcon CLOSE_ICON_SELECTED =
            new ImageIcon(EPTabbedPaneUI.class.getResource("/com/explodingpixels/widgets/images/close.png"));
    private static final ImageIcon CLOSE_ICON_UNSELECTED =
            new ImageIcon(EPTabbedPaneUI.class.getResource("/com/explodingpixels/widgets/images/close_unselected.png"));
    private static final Dimension CLOSE_BUTTON_BOUNDS =
            new Dimension(CLOSE_ICON_SELECTED.getIconWidth(), CLOSE_ICON_SELECTED.getIconHeight());

    @Override
    protected void installDefaults() {
        super.installDefaults();

        Font oldFont = tabPane.getFont();
        tabPane.setFont(oldFont.deriveFont(oldFont.getSize() - 2.0f));
        tabPane.setBorder(BorderFactory.createEmptyBorder());

        int leftPad = CLOSE_ICON_SELECTED.getIconWidth() + CLOSE_BUTTON_LEFT_PAD + CLOSE_BUTTON_RIGHT_PAD;
        tabInsets = new Insets(2, leftPad, 2, 10);
        selectedTabPadInsets = new Insets(0, 1, 0, 1);
    }

    @Override
    protected void installListeners() {
        super.installListeners();
        tabPane.addMouseMotionListener(createMouseMotionListener());
    }

    private MouseMotionListener createMouseMotionListener() {
        return new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                doMouseMotion(e.getX(), e.getY());
            }
        };
    }

    private void doMouseMotion(int x, int y) {
        int tabIndex = tabForCoordinate(tabPane, x, y);
        if (tabIndex >= 0 && tabIndex < tabPane.getTabCount()) {
            Rectangle tabBounds = getTabBounds(tabPane, tabIndex);
            System.out.println(tabIndex + ", " + isMouseOverCloseButton(tabBounds, x, y));
        }
    }

    private boolean isMouseOverCloseButton(Rectangle tabBounds, int mouseX, int mouseY) {
        Point closeLocation =
                getCloseButtonLocation(tabBounds.x, tabBounds.y, tabBounds.width, tabBounds.height);
        Rectangle closeBounds = new Rectangle(closeLocation, CLOSE_BUTTON_BOUNDS);
        return closeBounds.contains(mouseX, mouseY);
    }

    @Override
    protected Insets getContentBorderInsets(int tabPlacement) {
        return fPaintFullContentBorder ? FULL_CONTENT_BORDER_INSETS : HAIRLINE_BORDER_INSETS;
    }

    @Override
    public void paint(Graphics g, JComponent c) {
        ((Graphics2D) g).setRenderingHint(
                RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        super.paint(g, c);
        paintContentBorder(g, tabPane.getTabPlacement(), tabPane.getSelectedIndex());

    }

    @Override
    protected void paintTabBackground(Graphics g, int tabPlacement, int tabIndex, int x, int y,
                                      int width, int height, boolean isSelected) {
        Graphics2D graphics = (Graphics2D) g;

        graphics.setColor(isSelected ? SELECTED_BACKGROUND_COLOR : UNSELECTED_BACKGROUND_COLOR);
        graphics.fillRoundRect(x, y, width - 1, height, CORNER_ARC_DIAMETER, CORNER_ARC_DIAMETER);

        Point closeLocation = getCloseButtonLocation(x, y, width, height);
        ImageIcon closeImageIcon = isSelected ? CLOSE_ICON_SELECTED : CLOSE_ICON_UNSELECTED;
        graphics.drawImage(closeImageIcon.getImage(), closeLocation.x, closeLocation.y, null);
    }

    @Override
    protected void paintTabBorder(Graphics g, int tabPlacement, int tabIndex, int x, int y,
                                  int width, int height, boolean isSelected) {
        Graphics2D graphics = (Graphics2D) g;
        graphics.setColor(isSelected ? SELECTED_BORDER_COLOR : UNSELECTED_BORDER_COLOR);
        graphics.drawRoundRect(x, y, width - 1, height + CORNER_ARC_DIAMETER / 2,
                CORNER_ARC_DIAMETER, CORNER_ARC_DIAMETER);
    }

    @Override
    protected void paintFocusIndicator(Graphics g, int tabPlacement, Rectangle[] rects,
                                       int tabIndex, Rectangle iconRect, Rectangle textRect,
                                       boolean isSelected) {
        // do nothing.
    }

    @Override
    protected void paintContentBorderTopEdge(Graphics g, int tabPlacement, int selectedIndex, int x, int y, int width, int height) {
        Graphics2D graphics = (Graphics2D) g;

        graphics.translate(x, y);
        int borderHeight = getContentBorderInsets(tabPane.getTabPlacement()).top;
        fContentBorderTopEdgeBackgroundPainter.paint(graphics, tabPane, width, borderHeight);
        graphics.translate(-x, -y);

        graphics.setColor(Color.WHITE);
        Rectangle boundsOfSelectedTab = getTabBounds(tabPane, tabPane.getSelectedIndex());
        graphics.drawLine(boundsOfSelectedTab.x + 1, y, boundsOfSelectedTab.x + boundsOfSelectedTab.width - 2, y);
    }

    private Painter<Component> createContentBorderTopEdgeBackgroundPainter() {
        return new Painter<Component>() {
            public void paint(Graphics2D graphics, Component objectToPaint, int width, int height) {

                Paint paint = new GradientPaint(0, 0, Color.WHITE, 0, height - 1, new Color(0xf8f8f8));
                graphics.setPaint(paint);
                graphics.fillRect(0, 0, width, height - 1);
                graphics.setColor(SELECTED_BORDER_COLOR);
                graphics.drawLine(0, 0, width, 0);
                graphics.setColor(new Color(0x999999));
                // TODO figure out why we need to subtract off another extra pixel here -- doesn't make sense.
                graphics.drawLine(0, height - 2, width, height - 2);
            }
        };
    }

    @Override
    protected void paintContentBorderLeftEdge(Graphics g, int tabPlacement, int selectedIndex, int x, int y, int w, int h) {
        // do nothing.
    }

    @Override
    protected void paintContentBorderRightEdge(Graphics g, int tabPlacement, int selectedIndex, int x, int y, int w, int h) {
        // do nothing.
    }

    @Override
    protected void paintContentBorderBottomEdge(Graphics g, int tabPlacement, int selectedIndex, int x, int y, int w, int h) {
        // do nothing.
    }

    @Override
    protected int getTabLabelShiftX(int tabPlacement, int tabIndex, boolean isSelected) {
        return 0;
    }

    @Override
    protected int getTabLabelShiftY(int tabPlacement, int tabIndex, boolean isSelected) {
        return 0;
    }

    @Override
    protected int calculateTabWidth(int tabPlacement, int tabIndex, FontMetrics metrics) {
//        int preferredWidth = super.calculateTabWidth(tabPlacement, tabIndex, metrics);
        return fCurrentTabWidth;
    }

    public void setPaintsFullContentBorder(boolean paintsFullContentBorder) {
        fPaintFullContentBorder = paintsFullContentBorder;
        tabPane.repaint();
    }

    private Point getCloseButtonLocation(int tabLeftX, int tabTopY, int tabWidth,
                                         int tabHeight) {
        int closeX = tabLeftX + CLOSE_BUTTON_LEFT_PAD;
        int closeY = tabTopY + tabHeight / 2 - CLOSE_ICON_SELECTED.getIconHeight() / 2;
        return new Point(closeX, closeY);
    }
}