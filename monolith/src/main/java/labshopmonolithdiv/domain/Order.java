package labshopmonolithdiv.domain;

import java.util.Date;
import java.util.List;
import javax.persistence.*;
import labshopmonolithdiv.MonolithApplication;
import labshopmonolithdiv.domain.OrderPlaced;
import lombok.Data;

@Entity
@Table(name = "Order_table")
@Data
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String productId;

    private Integer qty;

    private String customerId;

    private Double amount;

    @PostPersist
    public void onPostPersist() {
        //Following code causes dependency to external APIs
        // it is NOT A GOOD PRACTICE. instead, Event-Policy mapping is recommended.

        labshopmonolithdiv.external.DecreaseStockCommand decreaseStockCommand = new labshopmonolithdiv.external.DecreaseStockCommand();
        // mappings goes here
        MonolithApplication.applicationContext
            .getBean(labshopmonolithdiv.external.InventoryService.class)
            .decreaseStock(/* get???(), */decreaseStockCommand);

        OrderPlaced orderPlaced = new OrderPlaced(this);
        orderPlaced.publishAfterCommit();
    }

    @PrePersist
    public void onPrePersist() {}

    public static OrderRepository repository() {
        OrderRepository orderRepository = MonolithApplication.applicationContext.getBean(
            OrderRepository.class
        );
        return orderRepository;
    }
}
