package in.mshitlearner.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name="MSH_CUSTOMERS")
public class Customer {
		
		@Id
		@Column(name="CUST_INDEX")
		private Integer index;
		@Column(name="CUSTOMER_ID")
		private String customer_id;
		@Column(name="FIRST_NAME")
		private String firstName;
		@Column(name="LAST_NAME")
		private String lastName;
		@Column(name="COMPANY")
		private String company;
		@Column(name="CITY")
		private String city;
		@Column(name="COUNTRY")
		private String country;
		@Column(name="PHONE_1")
		private String phone_1;
		@Column(name="PHONE_2")
		private String phone_2;
		@Column(name="EMAIL")
		private String email;
		@Column(name="SUBSCRIPTION_DATE")
		private String subscriptionDate;
		@Column(name="WEBSITE")
		private String website;
}
