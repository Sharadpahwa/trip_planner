package mobile.android.trip.planner.app.beans;


public class PlaceAutoComplete {

    private String place_id;
    private String description;
    private StructuredFormatting structured_formatting;

    public String getPlaceDesc() {
        return description;
    }

    public void setPlaceDesc(String placeDesc) {
        description = placeDesc;
    }

    public String getPlaceID() {
        return place_id;
    }

    public void setPlaceID(String placeID) {
        place_id = placeID;
    }

    public StructuredFormatting getStructuredFormatting() {
        return structured_formatting;
    }

    public void setStructuredFormatting(StructuredFormatting structuredFormatting) {
        this.structured_formatting = structuredFormatting;
    }

    public class StructuredFormatting {
        private String main_text;
        private String secondary_text;

        public String getMain_text() {
            return main_text;
        }

        public void setMain_text(String main_text) {
            this.main_text = main_text;
        }

        public String getSecondary_text() {
            return secondary_text;
        }

        public void setSecondary_text(String secondary_text) {
            this.secondary_text = secondary_text;
        }
    }

}
