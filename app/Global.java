import play.Application;
import play.GlobalSettings;

public class Global extends GlobalSettings {
    @Override
    public void onStart(Application app) {
       /* if (User.find.findRowCount() == 0) {
            Ebean.save((List<?>) Yaml.load("initial-data.yml"));
        }*/
    }
}