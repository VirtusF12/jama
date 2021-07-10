
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
import org.jfree.data.general.Dataset;
import org.jfree.data.time.TimeTableXYDataset;
import org.jfree.data.time.Year;
import org.jfree.data.xy.*;

import java.awt.Color;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JFrame;
import org.jfree.chart.*;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.labels.ItemLabelAnchor;
import org.jfree.chart.labels.ItemLabelPosition;
import org.jfree.chart.labels.XYItemLabelGenerator;
import org.jfree.chart.labels.StandardXYToolTipGenerator;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.AbstractXYDataset;
import org.jfree.data.xy.XYDataset;
import org.jfree.ui.TextAnchor;

import org.jfree.ui.*;
import org.jfree.util.ShapeUtilities;

public class main extends ApplicationFrame {

    private double[][] u,v;

    /**
     * Создаёт новый фрейм с графиком
     * @param title Заголовок окна
     */
    public main(String title, double[][] u, double[][] v) {
        super(title);
        this.u = u;
        this.v = v;
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

    static class LabeledXYDataset extends AbstractXYDataset {

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
            LabeledXYDataset labelSource = (LabeledXYDataset) dataset;
            return labelSource.getLabel(series, item);
        }

    }

    public XYDataset getXYDataset()
    {
        final XYSeries series1 = new XYSeries("Word");
        for (int i = 0; i < 13 ; i++) {
            System.out.println("(x="+u[i][0] + " : y="+u[i][1]+")");
            series1.add(u[i][0], u[i][1]);
        }

        final XYSeries series2 = new XYSeries("Document");
        for (int i = 0; i < 9 ; i++) {
            System.out.println("{x="+v[0][i] + " : y="+v[1][i]+"}");
            series2.add(v[0][i], v[1][i]);
        }

        final XYSeries series3 = new XYSeries("Third");
        series3.add (11.1, 4.4);
        series3.add ( 9.3, 3.6);
        series3.add ( 7.5, 2.8);
        series3.add ( 5.7, 3.9);
        series3.add ( 8.9, 6.6);
        series3.add ( 6.8, 3.4);
        series3.add (12.2, 4.3);
        series3.add (10.4, 3.2);

        final XYSeriesCollection dataset = new XYSeriesCollection();
        dataset.addSeries(series1);
        dataset.addSeries(series2);

        return dataset;
    }

    private JFreeChart createLineChart()
    {
        // Dataset dao     = new Dataset();
        XYDataset  dataset = getXYDataset();// dao.getXYDataset();

        final JFreeChart chart = ChartFactory.createXYLineChart(
                "Line Chart",             // chart title
                "X",                      // x axis label
                "Y",                      // y axis label
                dataset,                  // data
                PlotOrientation.VERTICAL,
                true, true, false);
        XYPlot plot = chart.getXYPlot();

        // Цвет фона графика
        plot.setBackgroundPaint(Color.lightGray);
        plot.setBackgroundAlpha(0.2f);

        // Цвет осей на диаграмме
        plot.setDomainGridlinePaint(Color.gray);
        plot.setRangeGridlinePaint (Color.gray);

        // Удаляем из диаграммы осевые линии
        ValueAxis axis = plot.getRangeAxis();  // RangeAxis
        axis.setAxisLineVisible(false);
        axis = plot.getDomainAxis();           // DomainAxis
        axis.setAxisLineVisible(false);

        // Определение отступа меток делений
        plot.setAxisOffset(new RectangleInsets (1.0, 1.0, 1.0, 1.0));

        // Исключаем представление линий 1-го графика
        XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
        renderer.setSeriesLinesVisible (0, false);
        renderer.setSeriesLinesVisible(1, false);
        Shape shape = ShapeUtilities.createDiagonalCross(0,3);
        renderer.setSeriesShape(0, shape);
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
        // Показываем стрелочку вправо
        domainAxis.setPositiveArrowVisible(true);
        // Задаем отступ от графика
        domainAxis.setUpperMargin(0.2);

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
        renderer.setSeriesPaint(1, Color.red);
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

    public static void showMatrix(double[][] matrix) {
        for (int i = 0; i < matrix.length; i++){
            for (int j = 0; j < matrix[0].length; j++) {
                System.out.print(String.format("%.2f",matrix[i][j]) + " ");
            }
            System.out.println();
        }
    }

    public static void showArr(String[] arr) {
        for (int i = 0; i < arr.length; i++)
            System.out.println(arr[i]);
        System.out.println();
    }

    public static void main(String[] args) {

        ArrayExceptWord arrayExceptWord = new ArrayExceptWord();
        List<String> uniqWord = new ArrayList<>();


        String[] arrHead = {
                "WikiLeaks Британская полиция знает о местонахождении основателя WikiLeaks ",
                "В суде США начинается процесс против россиянина рассылавшего спам",
                "Церемонию вручения Нобелевской премии мира бойкотируют стран",
                "В Великобритании арестован основатель сайта Wikileaks Джулиан Ассандж",
                "Украина игнорирует церемонию вручения Нобелевской премии",
                "Шведский суд отказался рассматривать апелляцию основателя Wikileaks",
                "НАТО и США разработали планы обороны стран Балтии против России",
                "Полиция Великобритании нашла основателя WikiLeaks но не арестовала",
                "В Стокгольме и Осло сегодня состоится вручение Нобелевских премий"
        };

        String str1 = "Заказать со скидкой  https://vk.cc/8uocjj\n" +
                "\n" +
                " Укрепляет мышцы и пресс\n" +
                " Экономит кучу времени\n" +
                " Гарантия 12 месяцев\n" +
                " Без предоплат!\n" +
                "\n" +
                " Быстрая доставка по России, Казахстану,  Украине, Беларуси!";
        String str2 = "Распродажа остатков склада СТИЛЬНЫХ МУЖСКИХ ЧАСОВ\n" +
                "СО СКИДКОЙ 50%\n" +
                "\n" +
                "Заказать со скидкой  https://vk.cc/8i6Vjt\n" +
                "\n" +
                "ОПЛАТА ПРИ ПОЛУЧЕНИИ НА ПОЧТЕ\n" +
                " ЭЛИТНЫЙ БРЕНД\n" +
                " СОВЕРШЕННЫЙ ДИЗАЙН\n" +
                " МИРОВАЯ ПОПУЛЯРНОСТЬ\n" +
                " ВЫСОКОЕ КАЧЕСТВО\n" +
                "\n" +
                " Быстрая доставка по России, Казахстану, Беларуси";
        String str3 = "5 часов на огороде, а я улыбаюсь! \n" +
                "От лопаты и тяпки ломит спину, натираются мозоли, гудят ноги\n" +
                "- Ничего этого нет, если работаешь ручным культиватором \"Торнадо\".\n" +
                "Вот его сайт  https://vk.cc/84joYP\n" +
                "\n" +
                "в 2 раза быстрее, чем с лопатой или тяпкой !может делать 6 видов работ ,на 80% снижает нагрузку на мышцы , больше ничего не болит на утро\n" +
                "\n" +
                "- Доставка почтой в любой город России! Оплата при получении\n" +
                " СКИДКА 53% по этой ссылке  https://vk.cc/84joYP";
        String str4 = "Мужской триммер с 3-мя насадками Micro Touch Solo!!!\n" +
                "\n" +
                " Идеальные контуры бороды\n" +
                " Чёткая линия бакенбард\n" +
                " Бритва может работать 45 минут от одного заряда\n" +
                " Бреет за один проход\n" +
                "\n" +
                "Заказать со скидкой: https://vk.cc/84j3Ta";
        String str5 = "ЭКШН КАМЕРА SPORTCAM FULLHD\n" +
                "+ PowerBank на 10000mAh В ПОДАРОК!\n" +
                "ЗАКАЖИ ПО АКЦИИ СЕЙЧАС  https://vk.cc/84iZP7\n" +
                "\n" +
                "Снимает на уровне GoPro\n" +
                "12 креплений в комплекте\n" +
                "Функция видеорегистратора\n" +
                "Аквабокс для съемки под водой\n" +
                "и еще много чего...\n" +
                "Подробнее: https://vk.cc/84iZP7\n" +
                "\n" +
                " Быстрая доставка по России.\n" +
                " Оплата ПОСЛЕ получения!\n" +
                "\n" +
                "ЗАКАЖИ ПО АКЦИИ СЕЙЧАС https://vk.cc/84iZP7";

        str5.replace("\n", "").replace(( String.valueOf((char)65440)), "").replace("\t", "");
        String[] arr = str5.split(" ");
        for (int i = 0; i < arr.length; i++) {
            System.out.print("("+arr[i] + ":["+arr[i].length()+"]) ");

            if (arr[i].contains("ПОДАРО")) {
                char symb = (char)arr[i].getBytes()[9];
                System.out.println("----" + (int)symb);
            }
        }


        System.out.println(arr.length + "\n\n");
        //showArr(arrHead);


        for (int i = 0; i < arrHead.length; i++) { // stop-simbol
            String temp = arrHead[i];
            String[] decomTemp = temp.split(" ");
            arrHead[i] = "";
            for (int j = 0; j < decomTemp.length; j++) {
                if (!arrayExceptWord.isExceptWord(decomTemp[j].toLowerCase()))
                    arrHead[i] += decomTemp[j] + " "; // в конце нужно пробел исключить
            }
        }

        for (int i = 0; i < arrHead.length; i++) { // porter
            String temp = arrHead[i];
            String[] decomTemp = temp.split(" ");
            arrHead[i] = "";
            for (int j = 0; j < decomTemp.length; j++) {
                arrHead[i] += Porter.stem(decomTemp[j]).toLowerCase() + " ";
                if (!uniqWord.contains(decomTemp[j].toLowerCase()))
                    uniqWord.add(decomTemp[j].toLowerCase());
            }

        }

        for (int i = 0; i < uniqWord.size(); i++) {
            System.out.print(uniqWord.get(i)+ " ");
        }

        //showArr(arrHead);

        System.out.println();


        double count = 0.;
        int countWord = uniqWord.size(), countDocument = arrHead.length;
        double[][] sequence = new double [countWord][countDocument];

        for (int i = 0; i < countWord; i++) {
            for (int j = 0; j < countDocument; j++) {
                count = 0.0;
                String[] decomDoc = arrHead[j].split(" ");
                for (int u = 0; u < decomDoc.length; u++) {
                    if (uniqWord.get(i).equals(decomDoc[u]))
                        count += 1.0;
                }
                sequence[i][j] = count; // занесение в ячейку частоты
            }
        }

        //showMatrix(sequence);
        System.out.println();


        double[][] a = {
                {1.0, 0.0, 0.0, 1.0, 0.0, 1.0, 0.0, 1.0, 0.0},
                {0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 1.0, 0.0},
                {0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 1.0, 0.0},
                {0.0, 0.0, 1.0, 0.0, 1.0, 0.0, 0.0, 0.0, 1.0},
                {0.0, 0.0, 1.0, 0.0, 1.0, 0.0, 0.0, 0.0, 1.0},
                {1.0, 0.0, 0.0, 1.0, 0.0, 1.0, 0.0, 1.0, 0.0},
                {1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0},
                {0.0, 0.0, 1.0, 0.0, 1.0, 0.0, 0.0, 0.0, 1.0},
                {0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0},
                {0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0},
                {0.0, 1.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0},
                {0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0},
                {0.0, 0.0, 1.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0},
        };


        showMatrix(a);
        Matrix matrix = new Matrix(a);

        SingularValueDecomposition svd = new SingularValueDecomposition(matrix);

        Matrix mU = svd.getU();
        Matrix mS = svd.getS();
        Matrix mV = svd.getV();

        double[][] matrixU = mU.getArray();
        showMatrix(matrixU);
        System.out.println();

        double[][] matrixS = mS.getArray();
        showMatrix(matrixS);
        System.out.println();

        double[][] matrixV = mV.getArray();
        showMatrix(matrixV);
        System.out.println();



        // Создаем новый фрейм
        main demo = new main("JFreeChart: StackedXYBarChart", matrixU, matrixV);
        demo.pack();
        // И показываем
        RefineryUtilities.centerFrameOnScreen(demo);
        demo.setVisible(true);

    }


}



