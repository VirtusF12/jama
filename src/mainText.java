
import Jama.Matrix;
import Jama.*;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

import java.awt.*;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import javax.swing.*;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.NumberTickUnit;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.labels.StandardXYItemLabelGenerator;
import org.jfree.chart.labels.StandardXYToolTipGenerator;
import org.jfree.chart.labels.XYItemLabelGenerator;
import org.jfree.chart.plot.*;
import org.jfree.chart.renderer.xy.StackedXYBarRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.time.TimeTableXYDataset;
import org.jfree.data.time.Year;
import org.jfree.data.xy.*;

import java.awt.Color;
import java.awt.Dimension;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.xy.AbstractXYDataset;
import org.jfree.data.xy.XYDataset;

import org.jfree.ui.*;
import org.jfree.util.ShapeUtilities;

import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.MongoClient;
import com.mongodb.DBObject;

// lib

public class mainText extends ApplicationFrame {

    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~ добавление работы с графикой
    private double[][] u,v;
    private int countW, countD;
    private ArrayList<String> listDoc;

    /**
     * Создаёт новый фрейм с графиком
     * @param title Заголовок окна
     */
    public mainText(String title, double[][] u, double[][] v, int countW, int countD, ArrayList<String> listDoc) {
        super(title);
        this.u = u; this.v = v;
        this.countW = countW; this.countD = countD;
        this.listDoc = listDoc;
        // Создаём новый график
        JFreeChart chart =  createLineChart(); // createChart(createDataset());

        // На панеле
        ChartPanel chartPanel = new ChartPanel(chart);
        // С размерами 450*450
        chartPanel.setPreferredSize(new Dimension(450, 450));
        // И ползунками если необходимо
        JScrollPane sp = new JScrollPane(chartPanel);
        sp.setPreferredSize(new Dimension(500, 500));
        sp.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        setContentPane(sp);
    }

    private static class LabeledXYDataset extends AbstractXYDataset {

        private static final int N = 26;
        private List<Number> x = new ArrayList<Number>(N);
        private List<Number> y = new ArrayList<Number>(N);
        private List<String> label = new ArrayList<String>(N);

        public void add(double x, double y, String label){
            this.x.add(x);
            this.y.add(y);
            this.label.add(label);
        }

        public String getLabel(int series, int item) {
            return label.get(item);
        }

        @Override
        public int getSeriesCount() {
            return 1;
        }

        @Override
        public Comparable getSeriesKey(int series) {
            return "Unit";
        }

        @Override
        public int getItemCount(int series) {
            return label.size();
        }

        @Override
        public Number getX(int series, int item) {
            return x.get(item);
        }

        @Override
        public Number getY(int series, int item) {
            return y.get(item);
        }
    }

    private static class LabelGenerator implements XYItemLabelGenerator {

        @Override
        public String generateLabel(XYDataset dataset, int series, int item) {
            main.LabeledXYDataset labelSource = (main.LabeledXYDataset) dataset;
            return labelSource.getLabel(series, item);
        }

    }

    public XYDataset getXYDataset()
    {
        System.out.println("\ncountW = " + countW + "; countD = " + countD);

        // System.out.println("-> ПОКАЗАТЬ СЛОВА");
        final XYSeries series1 = new XYSeries("W");
        for (int i = 0; i < countW ; i++) {
            // System.out.println("(x="+u[i][0] + " : y="+u[i][1]+")");
            series1.add(u[i][0], u[i][1]); // координаты для слов
        }
        // System.out.println("\n");

        System.out.println("-> ПОКАЗАТЬ ДОКУМЕНТЫ");
        final XYSeries series2 = new XYSeries("D");
        for (int i = 0; i < countD ; i++) {
            // String.format("%.2f",matrix[i][j])
            // System.out.println("["+i+"]: {x="+v[0][i] + " : y="+v[1][i]+"}");
            System.out.println("["+i+"]: {x="+String.format("%.2f",v[0][i]) + " : y="+String.format("%.2f",v[1][i])+"}");
            System.out.println("["+i+"]:" + listDoc.get(i));
            series2.add(v[0][i], v[1][i]); // координаты для документов
        }

        final XYSeriesCollection dataset = new XYSeriesCollection();
        // dataset.addSeries(series1); // показать слова
        dataset.addSeries(series2); // показать документы

        return dataset;
    }

    private JFreeChart createLineChart()
    {

        XYDataset  dataset = getXYDataset();// dao.getXYDataset();

        final JFreeChart chart = ChartFactory.createXYLineChart(
                "Документы (реклама)",             // chart title
                "X",                      // x axis label
                "Y",                      // y axis label
                dataset,                  // data
                PlotOrientation.VERTICAL,
                true, true, false);
        XYPlot plot = chart.getXYPlot();

        plot.setDomainZeroBaselineVisible(true); // показать ось y
        plot.setRangeZeroBaselineVisible(true); // показать ось x
        // XYPlot plot = (XYPlot) chart.getPlot();
        ValueAxis range = plot.getRangeAxis();
        range.setVisible(true);
        // Цвет фона графика
        plot.setBackgroundPaint(Color.lightGray);
        plot.setBackgroundAlpha(0.2f);
        // Цвет осей на диаграмме
        plot.setDomainGridlinePaint(Color.gray);
        plot.setRangeGridlinePaint (Color.gray);

        // Удаляем из диаграммы осевые линии
        ValueAxis axis = plot.getRangeAxis();  // RangeAxis
        axis.setAxisLineVisible(true); // показать ось
        axis = plot.getDomainAxis();           // DomainAxis
        axis.setAxisLineVisible(true); // показать ось

        // Определение отступа меток делений
        plot.setAxisOffset(new RectangleInsets (1.0, 1.0, 1.0, 1.0));

        // Исключаем представление линий 1-го графика
        XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
        renderer.setSeriesLinesVisible (0, false); // связь между линиями false
        renderer.setSeriesLinesVisible(1, false); // связь между линиями false
        //renderer.setBaseItemLabelsVisible(true);

        renderer.setSeriesPaint(0, Color.blue);
        renderer.setSeriesPaint(1, Color.red);

        // фигура
        Shape shape = ShapeUtilities.createDiagonalCross(0,3);
        renderer.setSeriesShape(1, shape);
        // renderer.setSeriesShape(0, shape);
        plot.setRenderer(renderer);

//        final Marker start = new ValueMarker(0.3);// new ValueMarker(0.3);
//        start.setPaint(Color.red);
//        start.setLabel("Current Value");
//        start.setLabelAnchor(RectangleAnchor.BOTTOM_LEFT);
//        start.setLabelTextAnchor(TextAnchor.TOP_LEFT);
//        plot.addRangeMarker(start);

        NumberFormat format = NumberFormat.getNumberInstance();
        format.setMaximumFractionDigits(2); // etc.
        XYItemLabelGenerator generator =
                new StandardXYItemLabelGenerator("{0} ({1};{2})", format, format);
        renderer.setBaseItemLabelGenerator(generator);
        renderer.setBaseItemLabelsVisible(true);





        return chart;
    }

    /**
     * Наполняет Set данными для построения графика
     * @return Данные для построения
     */
    private static TableXYDataset createDataset() {
        // Типа данные
        TimeTableXYDataset dataset = new TimeTableXYDataset();
        dataset.add(new Year(2002), 1000, "Blue");
        dataset.add(new Year(2003), 1100, "Blue");
        dataset.add(new Year(2002), 0, "Red");
        dataset.add(new Year(2003), 50, "Red");
        return dataset;
    }

    /**
     * Создаёт новый график по данным
     * @param dataset данные для построения
     * @return график
     */
    private static JFreeChart createChart(TableXYDataset dataset) {

        // OX - ось абсцисс
        // задаем название оси
        DateAxis domainAxis = new DateAxis("Year");
        domainAxis.setPositiveArrowVisible(true);// Показываем стрелочку вправо
        domainAxis.setUpperMargin(0.2); // Задаем отступ от графика

        // OY - ось ординат
        // Задаём название оси
        NumberAxis rangeAxis = new NumberAxis("Color");
        // Задаём величину деления
        rangeAxis.setStandardTickUnits(NumberAxis.createStandardTickUnits());
        rangeAxis.setTickUnit(new NumberTickUnit(200));
        // Показываем стрелочку вверх
        rangeAxis.setPositiveArrowVisible(true);

        // Render
        // Создаем стопковый (не знаю как лучше перевести) график
        // 0.02 - расстояние между столбиками
        StackedXYBarRenderer renderer = new StackedXYBarRenderer(0.02);
        // без рамки
        renderer.setDrawBarOutline(false);
        // цвета для каждого элемента стопки
        renderer.setSeriesPaint(0, Color.blue);
        renderer.setSeriesPaint(1, Color.blue);
        // Задаём формат и текст подсказки
        renderer.setBaseToolTipGenerator(new StandardXYToolTipGenerator("{0} : {1} = {2} tonnes", new SimpleDateFormat("yyyy"), new DecimalFormat("#,##0")));
        renderer.setSeriesItemLabelGenerator(0, new StandardXYItemLabelGenerator());
        renderer.setSeriesItemLabelGenerator(1, new StandardXYItemLabelGenerator());
        // Делаем её видимой
        renderer.setSeriesItemLabelsVisible(0, true);
        renderer.setSeriesItemLabelsVisible(1, true);
        // И описываем её шрифт
        renderer.setSeriesItemLabelFont(0, new Font("Serif", Font.BOLD, 10));
        renderer.setSeriesItemLabelFont(1, new Font("Serif", Font.BOLD, 10));

        // Plot
        // Создаем область рисования
        XYPlot plot = new XYPlot(dataset, domainAxis, rangeAxis, renderer);
        // Закрашиваем
        plot.setBackgroundPaint(Color.white);
        // Закрашиваем сетку
        plot.setDomainGridlinePaint(Color.white);
        plot.setRangeGridlinePaint(Color.white);
        // Отступ от осей
        plot.setAxisOffset(new RectangleInsets(0D, 0D, 10D, 10D));
        plot.setOutlinePaint(null);

        // Chart
        // Создаем новый график
        JFreeChart chart = new JFreeChart(plot);
        // Закрашиваем
        chart.setBackgroundPaint(Color.white);
        // Перемещаем легенду в верхний правый угол
        chart.getLegend().setPosition(RectangleEdge.RIGHT);
        chart.getLegend().setVerticalAlignment(VerticalAlignment.TOP);

        return chart;
    }
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~ окончание работы с граикой

    // исключить -+!^./:,\t\n\f\'
    private static ArrayList<String> getListWordsFilter(String data) {
        String result = data.replaceAll("[-+!^./:,\b\\t\\n\\f\\']"," ").toLowerCase();
        String[] arr = result.split(" ");
        ArrayList<String> list = new ArrayList<>();
        for (int i = 0; i < arr.length; i++)
            if (arr[i].length() > 2)
                list.add(arr[i]);
        return list;
    }

    // исключить стоп слова
    private static ArrayList<String> exceptWordsFilter(ArrayList<String> listWord){
        ArrayList<String> list = new ArrayList<>();
        ArrayExceptWord  arrayExceptWord = new ArrayExceptWord();
        for (String word : listWord)
            if (!arrayExceptWord.isExceptWord(word))
                list.add(word);

        return list;
    }

    // фильт Портера
    private static ArrayList<String> setPorterFilter(ArrayList<String> listWord){
        ArrayList<String> list = new ArrayList<>();
        for (String word : listWord)
            list.add(Porter.stem(word));

        return list;
    }

    // формирование списка строк
    private static ArrayList<String> getRow(ArrayList<ArrayList<String>> lists) {
        ArrayList<String> listRow = new ArrayList<>();
        for (int i = 0; i < lists.size(); i++) { // выбор списка 1
            for (int j = 0; j < lists.size(); j++) { // выбор списка 2
                if (i==j) continue;
                // -------
                for (String word : lists.get(i)) { // выбор слова из 1 списка
                    for (String wordIn : lists.get(j)) { // выбор слова из 2 списка
                        if (word.equals(wordIn) && (!listRow.contains(word)))
                            listRow.add(word);
                    }
                }
                // ------
            }
        }

        return listRow;
    }

    // показать результат
    private static void showListWord(ArrayList<String> list, String name) {
        System.out.println(name+"\n");
        for (int i = 0; i < list.size(); i++)
           System.out.print(list.get(i) + "(" + list.get(i).length() + ") ");
        System.out.println("\n");
    }

    // показать рещультаты матрицы
    public static void showMatrix(double[][] matrix) {
        for (int i = 0; i < matrix.length; i++){
            for (int j = 0; j < matrix[0].length; j++) {
                System.out.print(String.format("%.2f",matrix[i][j]) + " ");
            }
            System.out.println();
        }
    }

    public static void main(String[] args) {

        ArrayList<String> listDoc = new ArrayList<>(); // список документов
        ArrayList<ArrayList<String>> lists = new ArrayList<>(); // список, списка слов документа

        // ------------------------------------------------------------------
        System.out.println("--> Получение данных из БД <--");
        MongoClient mongoClient = new MongoClient("localhost", 27017);
        // объект db подключение к MongoDB серверу для указанной базы данных
        DB db = mongoClient.getDB("monitor-ads");
        if (db.collectionExists("ads-posts")) { // проверка существования коллекции
            int count = 0;
            DBCollection dbColl = db.getCollection("ads-posts");
            // получение всех объектов коллекции через метод find()
            DBCursor cursor = dbColl.find();
            while (cursor.hasNext()) {
                if (count == 100) break;
                DBObject dbObject = cursor.next();
                String text = (String) dbObject.get("text");
                listDoc.add(text);
                ArrayList<String> list = getListWordsFilter(text); // список слов без -+!^./:,\t\n\f\'
                // showListWord(list,"---> список слов без -+!^./:, <---");
                list = exceptWordsFilter(list);// showListWord(list,"---> список слов без стоп-слов <---");
                list = setPorterFilter(list);
                lists.add(list);
                // showListWord(list,"---> список слов (стемминг Портера) <---");
                // System.out.println("-------------------------------\n");
                count += 1;
            }
        } else System.out.println("no connection: bad-id");
        mongoClient.close(); // уничтожение экземпляра для очистки ресурса
        System.out.println("--> Закрытие соединения с БД <--");
        // ------------------------------------------------------------------

        ArrayList<String> list = getRow(lists);
        for (String word : list) {
            System.out.println(word);
        }

        // определение размерностей исходной матрицы слова на документы
        int sizeOfDoc = listDoc.size(); int sizeOfWord = list.size();

        // расчет частотной матрицы (слова на документы)
        double[][] matrix = new double[sizeOfWord][sizeOfDoc];
        for (int i = 0;i<sizeOfWord;i++){ // слова
            String word = list.get(i);
            //System.out.println("word["+i+"]: " + word);
            for(int j = 0;j<sizeOfDoc;j++){ // текст
                //System.out.println("doc["+j+"]: " + adsText[j]);
                if (listDoc.get(j).toLowerCase().contains(word))
                    matrix[i][j] += 1.0;
            }
        }

        // отображение частотной матрицы результатов
        /*for (int i = 0;i<sizeOfWord;i++){ // слова
            for(int j = 0;j<sizeOfDoc;j++){ // текст
                System.out.print(matrix[i][j] + " ");
            }
            System.out.println();
        }*/

        Matrix matrixNew = new Matrix(matrix);
        SingularValueDecomposition svd = new SingularValueDecomposition(matrixNew);
        Matrix mU = svd.getU(); // матрица слов (первые 2 столбца)
        Matrix mS = svd.getS(); // сингулярные числа
        Matrix mV = svd.getV(); // матрица документов (первые 2 строки)
        double[][] matrixU = mU.getArray(); // showMatrix(matrixU); System.out.println();
        double[][] matrixS = mS.getArray(); // showMatrix(matrixS); System.out.println();
        double[][] matrixV = mV.getArray(); // showMatrix(matrixV); System.out.println();

        // Создаем новый фрейм
        mainText demo = new mainText("JFreeChart: StackedXYBarChart", matrixU, matrixV, sizeOfWord, sizeOfDoc, listDoc);
        demo.pack();
        // И показываем
        RefineryUtilities.centerFrameOnScreen(demo);
        demo.setVisible(true);

    }
}
