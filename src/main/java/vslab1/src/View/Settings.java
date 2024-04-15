package vslab1.src.View;

import vslab1.src.View.Language.ELanguage;

public class Settings {
    private ELanguage activeLanguage = ELanguage.English;

    public Settings(ELanguage initLanguage) {
        activeLanguage = initLanguage;
    }

    public ELanguage getActiveLanguage() {
        return activeLanguage;
    }

    public void setActiveLanguage(ELanguage language) {
        this.activeLanguage = language;
    }

    public int getActiveLanguageOrdinal() {
        return activeLanguage.ordinal();
    }
}
