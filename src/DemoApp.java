import model.Employee;
import util.CustomSerializer;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;

public class DemoApp {

    public static void main(String[] args) {

        String file = "D:\\dev_edu\\STC13_HT\\HT06_CustomSerialize\\src\\dump\\data.xml";
        Employee employee = new Employee(99,45, "John", "Doe", false);
        CustomSerializer cs = null;
        try {
            cs = new CustomSerializer();
            cs.serialize(employee, file);
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (TransformerConfigurationException e) {
            e.printStackTrace();
        } catch (TransformerException e) {
            e.printStackTrace();
        }

        Employee newEmployee = (Employee) cs.deserialize(file);
        System.out.println(newEmployee);

    }
}
