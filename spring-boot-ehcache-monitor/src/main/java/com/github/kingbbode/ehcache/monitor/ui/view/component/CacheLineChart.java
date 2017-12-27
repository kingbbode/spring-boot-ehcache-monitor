package com.github.kingbbode.ehcache.monitor.ui.view.component;

import com.byteowls.vaadin.chartjs.ChartJs;
import com.byteowls.vaadin.chartjs.config.ChartConfig;
import com.byteowls.vaadin.chartjs.config.LineChartConfig;
import com.byteowls.vaadin.chartjs.data.Dataset;
import com.byteowls.vaadin.chartjs.data.LineDataset;
import com.byteowls.vaadin.chartjs.options.Position;
import com.github.kingbbode.ehcache.monitor.utils.DateTimeUtils;
import org.terracotta.statistics.archive.Timestamped;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by YG-MAC on 2017. 12. 18..
 */
public class CacheLineChart extends ChartJs {

    private static DateTimeFormatter FORMATTER_FOR_DISPLAY = DateTimeFormatter.ofPattern("HH:mm");

    public CacheLineChart(String name, List<Timestamped<Long>> history) {
        super();
        configure(createConfig(name, history));
        setJsLoggingEnabled(true);
    }

    private ChartConfig createConfig(String name, List<Timestamped<Long>> history) {
        LineChartConfig chartConfig = new LineChartConfig();
        chartConfig
                .data()
                .labels(history.stream().map(Timestamped::getTimestamp).map(timestamp -> DateTimeUtils.ofPattern(timestamp, FORMATTER_FOR_DISPLAY)).toArray(String[]::new))
                .addDataset(new LineDataset().type().label("hit").backgroundColor("rgba(151,187,205,0.5)").borderColor("white").borderWidth(2))
                .and();

        chartConfig.
                options()
                .responsive(true)
                .title()
                .display(true)
                .position(Position.TOP)
                .text(name)
                .and()
                .done();

        for (Dataset<?, ?> ds : chartConfig.data().getDatasets()) {
            if (ds instanceof LineDataset) {
                LineDataset lds = (LineDataset) ds;
                lds.dataAsList(history.stream().limit(3).map(Timestamped::getSample).map(Double::valueOf).collect(Collectors.toList()));
            }
        }
        return chartConfig;
    }
}
