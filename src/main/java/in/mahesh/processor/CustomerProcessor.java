package in.mahesh.processor;

import org.springframework.batch.item.ItemProcessor;

import in.mahesh.entity.Customer;

public class CustomerProcessor implements ItemProcessor<Customer, Customer>{
	
	@Override
	public Customer process(Customer customer) throws Exception {
		// TODO Auto-generated method stub
		if(customer.getCountry().equals("India"))
			return customer;
		else
			return null;
	}

}
