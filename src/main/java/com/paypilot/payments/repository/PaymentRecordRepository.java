package com.paypilot.payments.repository;
import com.paypilot.payments.model.PaymentRecord;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import java.time.LocalDate;
import java.util.List;
public interface PaymentRecordRepository extends JpaRepository<PaymentRecord,Long> {
 @Query("select p from PaymentRecord p where p.userId=:userId and p.dueDate between :fromDate and :toDate and (:category is null or lower(p.billCategory)=lower(:category)) order by p.dueDate,p.id")
 List<PaymentRecord> findForTracking(@Param("userId") String userId,@Param("category") String category,
  @Param("fromDate") LocalDate fromDate,@Param("toDate") LocalDate toDate);
}
