package com.github.kingbbode.ehcache.monitor;

import com.github.kingbbode.ehcache.monitor.utils.CacheValueReader;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Created by YG-MAC on 2018. 1. 3..
 */
public class CacheValueReaderTest {

    @Test
    public void 기본_데이터_타입의_String_변환이_제대로_되는가() throws Exception {
        assertEquals(Integer.toString(1), CacheValueReader.convert(1));
        assertEquals(Long.toString(1), CacheValueReader.convert(1L));
        assertEquals(Float.toString(1), CacheValueReader.convert(1.0F));
        assertEquals(Double.toString(1), CacheValueReader.convert(1.0));
    }

    @Test
    public void String_타입은_그대로_반환되는가() throws Exception {
        String test = "test";
        assertEquals(test, CacheValueReader.convert(test));
    }

    @Test
    public void Object_타입은_json_형태로_반환되는가() {
        
    }
}
