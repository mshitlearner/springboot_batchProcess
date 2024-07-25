package in.mshitlearner.respository;

import org.springframework.data.jpa.repository.JpaRepository;

import in.mshitlearner.entity.Customer;

public interface CustomerRepository extends JpaRepository<Customer, Integer>{

}
