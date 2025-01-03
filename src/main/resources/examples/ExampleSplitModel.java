import com.ivan.model.executor.model.repo.ExampleSplitModelRepository;
import com.ivan.model.orchestrator.repository.SplitRepository;
import org.springframework.transaction.annotation.Transactional;

//разделяемая модель
@SplitEntity(
        entity = ExamplePostgresModel.class,
        entityHandlerRepository = ExamplePostgresRepository.class,
        document = ExampleMongoModel.class,
        documentHandlerRepository = ExampleMongoRepository.class
)
public class ExampleSplitModel {
//    id для однозначной идентификации набора данных
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
//    поля различных типов хранилищ
    @Column
    private String postgresText;
    @Field
    private String mongoText;
    @MinioObject
    private byte[] minioObject;
}

//пример использования транзакции через AOP
@Transactional
public void doMutation(long id, ExampleSplitModelRepository repo){
    ExampleSplitModel data = repo.findById(id);
    data.postgresText = "new sql data";
    repo.save(data);
}

//репозиторий для взаимодействия с хранилищем
public interface ExampleSplitModelRepository
        extends SplitRepository<ExampleSplitModel, Long> {
    //получает набор методов по умолчанию
    //save(model)
    //findById(id)
    //deleteById(id)
}
