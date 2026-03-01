package ru.levin.apps.comparator.biz

import ru.levin.apps.comparator.biz.general.*
import ru.levin.apps.comparator.biz.repo.*
import ru.levin.apps.comparator.biz.stubs.*
import ru.levin.apps.comparator.biz.validation.*
import ru.levin.apps.comparator.common.ComparatorContext
import ru.levin.apps.comparator.common.ComparatorCorSettings
import ru.levin.apps.comparator.common.models.ComparatorCommand
import ru.levin.apps.comparator.cor.rootChain

class ComparatorProductProcessor(
    private val corSettings: ComparatorCorSettings = ComparatorCorSettings(),
) {

    suspend fun exec(ctx: ComparatorContext) {
        ctx.corSettings = corSettings
        chain.exec(ctx)
    }

    companion object {
        private val chain = rootChain<ComparatorContext> {
            initStatus("Initialize status")
            initRepo("Initialize repository")

            // ===== CREATE =====
            operation("Create product", ComparatorCommand.CREATE) {
                stubs("Process create stubs") {
                    stubCreateSuccess("Stub: create success")
                    stubValidationBadTitle("Stub: bad title")
                    stubValidationBadDescription("Stub: bad description")
                    stubValidationBadCategory("Stub: bad category")
                }
                validation("Create validation") {
                    trimFieldsProduct("Trim name and description")
                    validateNameNotEmpty("Validate name is not empty")
                    validateDescriptionNotEmpty("Validate description is not empty")
                    finishValidation("Finish create validation")
                }
                repoOps("Create in repository") {
                    repoCreate("Save to database")
                }
            }

            // ===== READ =====
            operation("Read product", ComparatorCommand.READ) {
                stubs("Process read stubs") {
                    stubReadSuccess("Stub: read success")
                    stubNotFound("Stub: not found")
                    stubValidationBadId("Stub: bad id")
                }
                validation("Read validation") {
                    trimFieldsProduct("Trim fields")
                    validateIdNotEmpty("Validate id is not empty")
                    validateIdProperFormat("Validate id format")
                    finishValidation("Finish read validation")
                }
                repoOps("Read from repository") {
                    repoRead("Read from database")
                }
            }

            // ===== UPDATE =====
            operation("Update product", ComparatorCommand.UPDATE) {
                stubs("Process update stubs") {
                    stubUpdateSuccess("Stub: update success")
                    stubValidationBadId("Stub: bad id")
                    stubValidationBadTitle("Stub: bad title")
                    stubValidationBadDescription("Stub: bad description")
                    stubValidationBadCategory("Stub: bad category")
                }
                validation("Update validation") {
                    trimFieldsProduct("Trim name and description")
                    validateIdNotEmpty("Validate id is not empty")
                    validateIdProperFormat("Validate id format")
                    validateNameNotEmpty("Validate name is not empty")
                    validateDescriptionNotEmpty("Validate description is not empty")
                    validateLockNotEmpty("Validate lock is not empty")
                    validateLockProperFormat("Validate lock format")
                    finishValidation("Finish update validation")
                }
                repoOps("Update in repository") {
                    repoUpdate("Update in database")
                }
            }

            // ===== DELETE =====
            operation("Delete product", ComparatorCommand.DELETE) {
                stubs("Process delete stubs") {
                    stubDeleteSuccess("Stub: delete success")
                    stubCannotDelete("Stub: cannot delete")
                    stubValidationBadId("Stub: bad id")
                }
                validation("Delete validation") {
                    trimFieldsProduct("Trim fields")
                    validateIdNotEmpty("Validate id is not empty")
                    validateIdProperFormat("Validate id format")
                    validateLockNotEmpty("Validate lock is not empty")
                    validateLockProperFormat("Validate lock format")
                    finishValidation("Finish delete validation")
                }
                repoOps("Delete from repository") {
                    repoDelete("Delete from database")
                }
            }

            // ===== SEARCH =====
            operation("Search products", ComparatorCommand.SEARCH) {
                stubs("Process search stubs") {
                    stubSearchSuccess("Stub: search success")
                    stubBadSearchString("Stub: bad search string")
                }
                validation("Search validation") {
                    trimFieldsSearch("Trim search string")
                    validateSearchStringLength("Validate search string length")
                    finishValidation("Finish search validation")
                }
                repoOps("Search in repository") {
                    repoSearch("Search in database")
                }
            }

            prepareResult("Prepare result")
        }
    }
}