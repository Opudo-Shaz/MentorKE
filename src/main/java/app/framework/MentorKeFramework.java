package app.framework;

import app.utility.helper.ClassScanner;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;

import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

@ApplicationScoped
public class MentorKeFramework {

    private Map<String, List<SelectBox>> formSelections = new HashMap<>();

    @PostConstruct
    public void init() {
        System.out.println("******** MentorKeFramework Contextual Instance created ********");
        resetFormSelections();
    }

    // ─── HTML FORM BUILDER ───────────────────────────────────────────────────

    public String htmlForm(Class<?> clazz) {

        if (!clazz.isAnnotationPresent(MentorKeForm.class))
            return "";

        MentorKeForm formAnnot = clazz.getAnnotation(MentorKeForm.class);

        StringBuilder form = new StringBuilder();
        form.append("<div class='container'>");
        form.append("<div class='card'>");
        form.append("<h2>").append(formAnnot.label().toUpperCase()).append("</h2><br/>");
        form.append("<form method='").append(formAnnot.method())
            .append("' action='").append(ActionMap.APP_PATH)
            .append(formAnnot.actionUrl()).append("'>");
        form.append("<div class='form-group'>");

        for (Field field : clazz.getDeclaredFields()) {
            if (!field.isAnnotationPresent(MentorKeField.class))
                continue;

            MentorKeField fieldInfo = field.getAnnotation(MentorKeField.class);
            form.append("<label>").append(fieldInfo.label()).append("</label>");

            String selectKey = fieldInfo.select();
            if (!selectKey.equalsIgnoreCase("") && formSelections.containsKey(selectKey)) {
                form.append("<select name='").append(field.getName()).append("' required>");
                form.append("<option value=''>-- Select ").append(fieldInfo.label()).append(" --</option>");
                formSelections.get(selectKey).forEach(s ->
                    form.append("<option value='").append(s.getValue()).append("'>")
                        .append(s.getName()).append("</option>"));
                form.append("</select>");
            } else if (fieldInfo.type().equalsIgnoreCase("textarea")) {
                form.append("<textarea name='")
                    .append(fieldInfo.name().isEmpty() ? field.getName() : fieldInfo.name())
                    .append("' placeholder='").append(fieldInfo.placeholder())
                    .append("' rows='4' required></textarea>");
            } else if (fieldInfo.type().equalsIgnoreCase("password")) {
                form.append("<input type='password' name='")
                    .append(fieldInfo.name().isEmpty() ? field.getName() : fieldInfo.name())
                    .append("' placeholder='").append(fieldInfo.placeholder())
                    .append("' required />");
            } else if (fieldInfo.type().equalsIgnoreCase("email")) {
                form.append("<input type='email' name='")
                    .append(fieldInfo.name().isEmpty() ? field.getName() : fieldInfo.name())
                    .append("' placeholder='").append(fieldInfo.placeholder())
                    .append("' required />");
            } else if (fieldInfo.type().equalsIgnoreCase("number")) {
                form.append("<input type='number' name='")
                    .append(fieldInfo.name().isEmpty() ? field.getName() : fieldInfo.name())
                    .append("' placeholder='").append(fieldInfo.placeholder())
                    .append("' required />");
            } else {
                form.append("<input type='text' name='")
                    .append(fieldInfo.name().isEmpty() ? field.getName() : fieldInfo.name())
                    .append("' placeholder='").append(fieldInfo.placeholder())
                    .append("' required />");
            }
        }

        resetFormSelections();

        form.append("</div>");
        form.append("<button type='submit' class='btn'>Submit</button>");
        form.append("</form>");
        form.append("</div>");
        form.append("</div>");

        return form.toString();
    }

    // ─── HTML TABLE BUILDER ──────────────────────────────────────────────────

    public String htmlTable(Class<?> clazz, List<?> tableData) {

        if (!clazz.isAnnotationPresent(MentorKeTable.class))
            return "";

        MentorKeTable tableAnnot = clazz.getAnnotation(MentorKeTable.class);

        StringBuilder table = new StringBuilder();
        table.append("<div class='container'>");
        table.append("<div class='card'>");
        table.append("<h2>").append(tableAnnot.label()).append("</h2>");

        if (!tableAnnot.addLink().equalsIgnoreCase(""))
            table.append("<a href='").append(ActionMap.APP_PATH)
                .append(tableAnnot.addLink())
                .append("' class='btn' style='margin:10px 0;display:inline-block;'>")
                .append("&#43; Add ").append(tableAnnot.label()).append("</a>");

        table.append("<table>");

        // Collect column metadata from @MentorKeTableCol fields
        record ColMeta(String fieldName, String label) {}
        List<ColMeta> cols = new ArrayList<>();
        for (Field field : clazz.getDeclaredFields()) {
            if (!field.isAnnotationPresent(MentorKeTableCol.class))
                continue;
            cols.add(new ColMeta(field.getName(),
                field.getAnnotation(MentorKeTableCol.class).label()));
        }

        int colWidth = 96 / cols.size();

        // Table header
        table.append("<thead><tr>");
        for (ColMeta col : cols)
            table.append("<th style='width:").append(colWidth).append("%;'>")
                .append(col.label()).append("</th>");
        table.append("<th style='width:2%;'></th>");  
        table.append("<th style='width:2%;'></th>");
        table.append("</tr></thead>");

        // Table body
        table.append("<tbody>");
        for (Object data : tableData) {
            table.append("<tr>");
            for (ColMeta col : cols) {
                try {
                    Field field = data.getClass().getDeclaredField(col.fieldName());
                    field.setAccessible(true);
                    table.append("<td>").append(field.get(data)).append("</td>");
                } catch (Exception e) {
                    throw new RuntimeException("Error reading field: " + col.fieldName(), e);
                }
            }

            // Edit and delete buttons
            table.append("<td class='actions'>");
            try {
                Field idField = findIdField(clazz);
                idField.setAccessible(true);
                Object idValue = idField.get(data);

                //  Edit button — uses the entity's own edit link
                table.append("<a href='").append(ActionMap.APP_PATH)
                    .append(tableAnnot.editLink()).append("/").append(idValue)
                    .append("' class='icon-btn edit-btn' title='Edit'>")
                    .append("<i class='fa-solid fa-pen'></i>")
                    .append("</a>");

                //Delete button — uses the entity's own delete link
                table.append("<a href='").append(ActionMap.APP_PATH)
                    .append(tableAnnot.deleteLink()).append("/").append(idValue)
                    .append("' class='icon-btn delete-btn' title='Delete'")
                    .append(" onclick='return confirm(\"Are you sure you want to delete this record?\")'")
                    .append("><i class='fa-solid fa-trash'></i></a>");

            } catch (Exception e) {
                throw new RuntimeException("Error building action buttons", e);
            }
            table.append("</td></tr>");
        }
        table.append("</tbody></table>");
        table.append("</div></div>");

        return table.toString();
    }

    // ─── NAV MENU GENERATOR ──────────────────────────────────────────────────

    public String generateMenuItem() {
        return ClassScanner.scanForAction("app.action").stream()
            .map(clazz -> clazz.getAnnotation(Action.class))
            .filter(Objects::nonNull)
            .filter(Action::showLink)
            .sorted(Comparator.comparingInt(Action::linkPosition))
            .map(a -> "<a href='" + ActionMap.APP_PATH + a.value() + "/" + a.pageLink() + "'>"
                + a.label() + "</a>")
            .collect(Collectors.joining("\n"));
    }

    // ─── FORM SELECTIONS ─────────────────────────────────────────────────────

    public Map<String, List<SelectBox>> getFormSelections() { return formSelections; }
    public void setFormSelections(Map<String, List<SelectBox>> formSelections) {
        this.formSelections = formSelections;
    }

    public void resetFormSelections() {
        formSelections = new HashMap<>();

        // Gender
        formSelections.put("gender", List.of(
            SelectBox.builder().value("Male").name("Male").build(),
            SelectBox.builder().value("Female").name("Female").build(),
            SelectBox.builder().value("Non-Binary").name("Non-Binary").build()
        ));

        // Education levels — matches your MenteeBean validation exactly
        formSelections.put("educationLevel", List.of(
            SelectBox.builder().value("Primary").name("Primary").build(),
            SelectBox.builder().value("Secondary").name("Secondary").build(),
            SelectBox.builder().value("High School").name("High School").build(),
            SelectBox.builder().value("Certificate").name("Certificate").build(),
            SelectBox.builder().value("Diploma").name("Diploma").build(),
            SelectBox.builder().value("Undergraduate").name("Undergraduate").build(),
            SelectBox.builder().value("Postgraduate").name("Postgraduate").build(),
            SelectBox.builder().value("PhD").name("PhD").build(),
            SelectBox.builder().value("Other").name("Other").build()
        ));

        // Mentorship areas / specializations
        formSelections.put("specialization", List.of(
            SelectBox.builder().value("Software Development").name("Software Development").build(),
            SelectBox.builder().value("Data Science").name("Data Science").build(),
            SelectBox.builder().value("Cybersecurity").name("Cybersecurity").build(),
            SelectBox.builder().value("UI/UX Design").name("UI/UX Design").build(),
            SelectBox.builder().value("Project Management").name("Project Management").build(),
            SelectBox.builder().value("Cloud Computing").name("Cloud Computing").build(),
            SelectBox.builder().value("Machine Learning").name("Machine Learning").build(),
            SelectBox.builder().value("Business Analysis").name("Business Analysis").build(),
            SelectBox.builder().value("Other").name("Other").build()
        ));

        // User roles
        formSelections.put("role", List.of(
            SelectBox.builder().value("Mentor").name("Mentor").build(),
            SelectBox.builder().value("Mentee").name("Mentee").build(),
            SelectBox.builder().value("Admin").name("Admin").build()
        ));

        // Mentee / Mentor status
        formSelections.put("status", List.of(
            SelectBox.builder().value("Active").name("Active").build(),
            SelectBox.builder().value("Inactive").name("Inactive").build(),
            SelectBox.builder().value("Suspended").name("Suspended").build()
        ));

        // Years of experience
        formSelections.put("yearsOfExperience", List.of(
            SelectBox.builder().value("1").name("1 year").build(),
            SelectBox.builder().value("2").name("2 years").build(),
            SelectBox.builder().value("3").name("3 years").build(),
            SelectBox.builder().value("5").name("5 years").build(),
            SelectBox.builder().value("10").name("10+ years").build()
        ));
    }

    // ─── PRIVATE HELPERS ─────────────────────────────────────────────────────

    // Searches both the class and its superclass for an "id" field
    private Field findIdField(Class<?> clazz) throws NoSuchFieldException {
        try {
            return clazz.getDeclaredField("id");
        } catch (NoSuchFieldException e) {
            if (clazz.getSuperclass() != null)
                return clazz.getSuperclass().getDeclaredField("id");
            throw e;
        }
    }
}