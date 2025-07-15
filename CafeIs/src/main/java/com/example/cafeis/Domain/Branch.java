package com.example.cafeis.Domain;

import com.example.cafeis.Enum.PurchasePointStatus;
import jakarta.persistence.*;
import lombok.*;


@Entity
@Table(name = "tbl_branch", indexes = {
        @Index(name = "idx_branch_status", columnList = "operating_status"),
        @Index(name = "idx_branch_title", columnList = "branch_title"),
        @Index(name = "idx_branch_text", columnList = "location_text"),
        @Index(name = "idx_branch_status_title", columnList = "operating_status, branch_title")
})
@Getter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Branch {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "branch_no")
    private Long branchNo;

    @Column(name = "branch_title", length = 100, nullable = false)
    private String branchTitle;

    @Column(name = "location_text", length = 200, nullable = false)
    private String locationText;

    @Column(name = "seat_count")
    @Builder.Default
    private Integer seatCount = 0;

    @Column(name = "open_schedule_json", columnDefinition = "JSON")
    private String openScheduleJson;

    @Enumerated(EnumType.STRING)
    @Column(name = "operating_status", length = 30)
    @Builder.Default
    private PurchasePointStatus operatingStatus = PurchasePointStatus.OPEN;

    @Column(name = "operation_note", length = 200)
    private String operationNote;


}
