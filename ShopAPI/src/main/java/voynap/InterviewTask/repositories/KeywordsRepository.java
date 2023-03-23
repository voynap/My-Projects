package voynap.InterviewTask.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import voynap.InterviewTask.models.Keyword;

@Repository
public interface KeywordsRepository extends JpaRepository<Keyword, Integer> {
}
