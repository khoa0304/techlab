package lab.Solr;

import org.apache.solr.client.solrj.beans.Field;

public class ProductBean 
{
    @Field("id")
    long id;
    @Field("name")
    String name;
    @Field
    double price;
    
    public ProductBean() {} // Empty constructor is required
    
    public ProductBean(long id, String name, double price)
    {
        super();
        this.id = id;
        this.name = name;
        this.price = price;
    }
    
    // Getter Setters
    public long getId() {
        return id;
    }
    public void setId(long id) {
        this.id = id;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public double getPrice() {
        return price;
    }
    public void setPrice(double price) {
        this.price = price;
    }
}
