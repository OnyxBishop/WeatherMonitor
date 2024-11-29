package ramStudio.extensions;

import java.util.HashMap;
import java.util.Map;

public class Coordinates {
    private Map<CityCodes, StringPair> countriesKvp;

    public Coordinates() {
        countriesKvp = new HashMap<CityCodes, StringPair>();

        countriesKvp.put(CityCodes.MSK, new StringPair("55.75", "37.62"));
        countriesKvp.put(CityCodes.TMN, new StringPair("57.15298462", "65.54122925"));
        countriesKvp.put(CityCodes.SPB, new StringPair("59.93867493", "30.31449318"));
    }

    public StringPair get(CityCodes cityCode) {
        if(!countriesKvp.containsKey(cityCode))
            return null;

        return countriesKvp.get(cityCode);
    }
}
