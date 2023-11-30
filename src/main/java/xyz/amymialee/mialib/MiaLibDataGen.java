package xyz.amymialee.mialib;

import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import org.jetbrains.annotations.NotNull;

public class MiaLibDataGen extends MDataGen {
    @Override
    protected void generateTranslations(FabricLanguageProvider.@NotNull TranslationBuilder builder) {
        builder.add("mvalue.mialib.test_boolean", "Test Boolean");
    }
}
