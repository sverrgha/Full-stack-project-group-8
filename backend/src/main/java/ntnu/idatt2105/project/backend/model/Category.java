package ntnu.idatt2105.project.backend.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Category {
  Long id;
  String nameEn;
  String nameNo;
  String url;
}
