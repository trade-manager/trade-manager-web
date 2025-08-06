package org.trade.core.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.StreamReadConstraints;
import com.fasterxml.jackson.core.StreamWriteConstraints;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.modelmapper.spi.MappingContext;
import org.trade.core.dao.Aspect;
import org.trade.core.util.time.TradingCalendar;

import java.text.SimpleDateFormat;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.TimeZone;

public class JSONMapper {

    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final ModelMapper modelMapper;
    private static final String dateFormat = "yyyy-MM-dd HH:mm:ss -HH:mm";

    static {

        objectMapper.registerModule(new JavaTimeModule());
        // Set max depth
        StreamReadConstraints readConstraints = StreamReadConstraints.builder().maxNestingDepth(200).build();
        StreamWriteConstraints writeConstraints = StreamWriteConstraints.builder().maxNestingDepth(200).build();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);

        SimpleDateFormat df = new SimpleDateFormat(dateFormat);
        objectMapper.setDateFormat(df);
        objectMapper.setTimeZone(TimeZone.getTimeZone(TradingCalendar.MKT_TIMEZONE));
        objectMapper.getFactory().setStreamReadConstraints(readConstraints);
        objectMapper.getFactory().setStreamWriteConstraints(writeConstraints);

        modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        modelMapper.addConverter(new ZonedDateTimeConverter());
    }

    /**
     * @param aspect Object
     * @return json String
     * @throws JsonProcessingException exception
     */
    public static String getJSONString(Object aspect) throws JsonProcessingException {

        return objectMapper.writeValueAsString(aspect);
    }

    /**
     * @param json  String
     * @param clazz Class<T>
     * @return <T> T
     * @throws JsonProcessingException exception
     */
    public static <T> T getDTO(String json, Class<T> clazz) throws JsonProcessingException {

        return objectMapper.readValue(json, clazz);
    }

    public static <T> T convertToDto(Aspect aspect, Class<T> clazz) {

        return modelMapper.map(aspect, clazz);
    }

    public static <T> T convertToEntity(Aspect aspect, Class<T> clazz) {

        return modelMapper.map(aspect, clazz);
    }

    static class ZonedDateTimeConverter implements Converter<ZonedDateTime, String> { // Example: ZonedDateTime to String
        @Override
        public String convert(MappingContext<ZonedDateTime, String> context) {
            ZonedDateTime source = context.getSource();

            if (source == null) {
                return null;
            }
            // Define your desired format
            return source.format(DateTimeFormatter.ISO_ZONED_DATE_TIME);
        }
    }
}
