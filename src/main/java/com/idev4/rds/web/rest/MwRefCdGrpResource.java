package com.idev4.rds.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.idev4.rds.domain.MwRefCdGrp;
import com.idev4.rds.dto.CodeGroupDto;
import com.idev4.rds.service.MwRefCdGrpService;
import io.github.jhipster.web.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * REST controller for managing MwRefCdGrp.
 */
@RestController
@RequestMapping("/api")
public class MwRefCdGrpResource {

    private static final String ENTITY_NAME = "mwRefCdGrp";
    private final Logger log = LoggerFactory.getLogger(MwRefCdGrpResource.class);
    private final MwRefCdGrpService mwRefCdGrpService;

    public MwRefCdGrpResource(MwRefCdGrpService mwRefCdGrpService) {
        this.mwRefCdGrpService = mwRefCdGrpService;
    }

    /**
     * POST /mw-ref-cd-grps : Create a new mwRefCdGrp.
     *
     * @param mwRefCdGrp the mwRefCdGrp to create
     * @return the ResponseEntity with status 201 (Created) and with body the new mwRefCdGrp, or with status 400 (Bad Request) if the
     * mwRefCdGrp has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/create-ref-cd-grps")
    @Timed
    public ResponseEntity<Map> createMwRefCdGrp(@RequestBody CodeGroupDto codeGroup) throws URISyntaxException {
        log.debug("REST request to save MwRefCdGrp : {}", codeGroup);
        Map<String, String> resp = new HashMap<String, String>();
        String currUser = SecurityContextHolder.getContext().getAuthentication().getName();

        if (codeGroup.groupCode == null || codeGroup.groupCode.isEmpty()) {
            resp.put("error", "Group Code Missing !!");
            return ResponseEntity.badRequest().body(resp);
        }

        if (codeGroup.groupName == null || codeGroup.groupName.isEmpty()) {
            resp.put("error", "Group Name Missing !!");
            return ResponseEntity.badRequest().body(resp);
        }

        Map<String, MwRefCdGrp> respData = new HashMap<String, MwRefCdGrp>();
        // MwRefCdGrp group = mwRefCdGrpService.createNewCodesGroup(codeGroup,currUser);
        // respData.put("group", group);
        return ResponseEntity.ok().body(respData);

    }

    /**
     * PUT /mw-ref-cd-grps : Updates an existing mwRefCdGrp.
     *
     * @param mwRefCdGrp the mwRefCdGrp to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated mwRefCdGrp, or with status 400 (Bad Request) if the
     * mwRefCdGrp is not valid, or with status 500 (Internal Server Error) if the mwRefCdGrp couldn't be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/update-ref-cd-grps")
    @Timed
    public ResponseEntity<Map> updateMwRefCdGrp(@RequestBody CodeGroupDto codeGroup) throws URISyntaxException {
        log.debug("REST request to update MwRefCdGrp : {}", codeGroup);

        Map<String, String> resp = new HashMap<String, String>();
        String currUser = SecurityContextHolder.getContext().getAuthentication().getName();

        if (codeGroup.groupCode == null || codeGroup.groupCode.isEmpty()) {
            resp.put("error", "Group Code Missing !!");
            return ResponseEntity.badRequest().body(resp);
        }

        if (codeGroup.groupName == null || codeGroup.groupName.isEmpty()) {
            resp.put("error", "Group Name Missing !!");
            return ResponseEntity.badRequest().body(resp);
        }

        Map<String, MwRefCdGrp> respData = new HashMap<String, MwRefCdGrp>();
        MwRefCdGrp group = mwRefCdGrpService.updateExistingCodesGroup(codeGroup, currUser);
        if (group == null) {
            resp.put("error", "Common Group Value Not Found !!");
            return ResponseEntity.badRequest().body(resp);
        }
        respData.put("group", group);
        return ResponseEntity.ok().body(respData);
    }

    /**
     * GET /mw-ref-cd-grps/:id : get the "id" mwRefCdGrp.
     *
     * @param id the id of the mwRefCdGrp to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the mwRefCdGrp, or with status 404 (Not Found)
     */
    @GetMapping("/mw-ref-cd-grps/{id}")
    @Timed
    public ResponseEntity<MwRefCdGrp> getMwRefCdGrp(@PathVariable Long id) {
        log.debug("REST request to get MwRefCdGrp : {}", id);
        MwRefCdGrp mwRefCdGrp = mwRefCdGrpService.findOne(id);
        return ResponseUtil.wrapOrNotFound(Optional.ofNullable(mwRefCdGrp));
    }

    /**
     * DELETE /mw-ref-cd-grps/:id : delete the "id" mwRefCdGrp.
     *
     * @param id the id of the mwRefCdGrp to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    // @DeleteMapping("/mw-ref-cd-grps/{id}")
    // @Timed
    // public ResponseEntity<Void> deleteMwRefCdGrp(@PathVariable Long id) {
    // log.debug("REST request to delete MwRefCdGrp : {}", id);
    // mwRefCdGrpService.delete(id);
    // return ResponseEntity.ok();
    // }
    @GetMapping("/testing-rds-service")
    public String doTest() {
        return "Tesging Teesgint";
    }
}
