package note.service;

import common.exception.NoteIdDoesNotExists;
import note.dto.NoteMapper;
import note.dto.NoteRequestDTO;
import note.dto.NoteResponseDTO;
import note.model.Note;
import note.repository.NoteRepository;
import note.repository.NoteRepositoryMysql;
import note.service.NoteService;
import note.service.NoteServiceImpl;
import note.repository.NoteRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class NoteServiceImplTest {
}
