package taco.klkl.domain.member.converter;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import taco.klkl.domain.member.domain.Gender;

@Converter(autoApply = true)
public class GenderConverter implements AttributeConverter<Gender, String> {

	@Override
	public String convertToDatabaseColumn(final Gender gender) {
		if (gender == null) {
			return null;
		}
		return gender.getValue();
	}

	@Override
	public Gender convertToEntityAttribute(final String dbData) {
		if (dbData == null) {
			return null;
		}
		return Gender.from(dbData);
	}
}
