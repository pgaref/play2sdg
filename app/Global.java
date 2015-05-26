import java.util.List;

import models.RelationalUser;
import play.Application;
import play.GlobalSettings;
import play.libs.Yaml;

import com.avaje.ebean.Ebean;

public class Global extends GlobalSettings {
    @Override
    public void onStart(Application app) {
        if (RelationalUser.find.findRowCount() == 0) {
            Ebean.save((List<?>) Yaml.load("initial-data.yml"));
        }
    }
}