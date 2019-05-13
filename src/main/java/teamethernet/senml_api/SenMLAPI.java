package teamethernet.senml_api;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import javafx.util.Pair;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

class SenMLAPI<T extends Formatter> {

    private static final String STRING_INSTANCE = "";
    private static final Double DOUBLE_INSTANCE = 0.0;
    private static final Integer INTEGER_INSTANCE = 0;
    private static final Boolean BOOLEAN_INSTANCE = false;

    private final T formatter;

    private SenMLAPI(final T formatter) {
        this.formatter = formatter;
    }

    static SenMLAPI<JsonFormatter> initJsonEncode() {
        return new SenMLAPI<>(new JsonFormatter());
    }

    static SenMLAPI<CborFormatter> initCborEncode() {
        return new SenMLAPI<>(new CborFormatter());
    }

    static SenMLAPI<JsonFormatter> initJsonDecode(final String buffer) throws IOException {
        return new SenMLAPI<>(new JsonFormatter(buffer));
    }

    static SenMLAPI<CborFormatter> initCborDecode(final byte[] buffer) throws IOException {
        return new SenMLAPI<>(new CborFormatter(buffer));
    }

    String getRecord(final int recordIndex) {
        return formatter.getRecords().get(recordIndex).toString();
    }

    List<Label> getLabels(final int recordIndex) {
        final JsonNode record = formatter.getRecords().get(recordIndex);
        final List<Label> labels = new ArrayList<>();

        record.fields().forEachRemaining(field -> labels.add(Label.NAME_TO_VALUE_MAP.get(field.getKey())));

        return labels;
    }

    @SuppressWarnings("unchecked")
    <S> S getValue(Label<S> label, int recordIndex) {
        final Class<S> type = label.getClassType();
        final JsonNode record = formatter.getRecords().get(recordIndex);

        if (type.isInstance(STRING_INSTANCE)) {
            return type.cast(formatter.getStringValue((Label<String>) label, record));
        } else if (type.isInstance(DOUBLE_INSTANCE)) {
            return type.cast(formatter.getDoubleValue((Label<Double>) label, record));
        } else if (type.isInstance(INTEGER_INSTANCE)) {
            return type.cast(formatter.getIntegerValue((Label<Integer>) label, record));
        } else if (type.isInstance(BOOLEAN_INSTANCE)) {
            return type.cast(formatter.getBooleanValue((Label<Boolean>) label, record));
        } else {
            throw new UnsupportedOperationException(
                    type + " is not supported. Use String, Double, Integer or Boolean");
        }
    }

    @SafeVarargs
    final <S> void addRecord(final Pair<Label<S>, S>... pairs) {
        final JsonNode record = formatter.getMapper().createObjectNode();

        for (final Pair<Label<S>, S> pair : pairs) {
            final Class<S> type = pair.getKey().getClassType();

            if (type.isInstance(STRING_INSTANCE)) {
                ((ObjectNode) record).put(pair.getKey().toString(), (String) pair.getValue());
            } else if (type.isInstance(DOUBLE_INSTANCE)) {
                ((ObjectNode) record).put(pair.getKey().toString(), (Double) pair.getValue());
            } else if (type.isInstance(INTEGER_INSTANCE)) {
                ((ObjectNode) record).put(pair.getKey().toString(), (Integer) pair.getValue());
            } else if (type.isInstance(BOOLEAN_INSTANCE)) {
                ((ObjectNode) record).put(pair.getKey().toString(), (Boolean) pair.getValue());
            } else {
                throw new UnsupportedOperationException(
                        type + " is not supported. Use String, Double, Integer or Boolean");
            }
        }

        ((ArrayNode) formatter.getRecords()).add(record);
    }

    String endSenML() throws IOException {
        return formatter.endSenML(formatter.getRecords());
    }

}
