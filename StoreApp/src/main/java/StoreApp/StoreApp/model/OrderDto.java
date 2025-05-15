package StoreApp.StoreApp.model;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.sql.Date;
import java.util.List;


@Data
public class OrderDto {
	private int id;
	private int total;
	private Date booking_date;
	private String payment_method;
	private String status;
	private String fullname;
	private String country;
	private String address;
	private String phone;
	private String email;
	private String note;
	private List<Order_ItemDto> order_item;
	private UserDto user;
}
