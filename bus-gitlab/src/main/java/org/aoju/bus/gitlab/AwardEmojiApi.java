/*********************************************************************************
 *                                                                               *
 * The MIT License (MIT)                                                         *
 *                                                                               *
 * Copyright (c) 2015-2020 aoju.org Greg Messner and other contributors.         *
 *                                                                               *
 * Permission is hereby granted, free of charge, to any person obtaining a copy  *
 * of this software and associated documentation files (the "Software"), to deal *
 * in the Software without restriction, including without limitation the rights  *
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell     *
 * copies of the Software, and to permit persons to whom the Software is         *
 * furnished to do so, subject to the following conditions:                      *
 *                                                                               *
 * The above copyright notice and this permission notice shall be included in    *
 * all copies or substantial portions of the Software.                           *
 *                                                                               *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR    *
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,      *
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE   *
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER        *
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, *
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN     *
 * THE SOFTWARE.                                                                 *
 ********************************************************************************/
package org.aoju.bus.gitlab;

import org.aoju.bus.gitlab.models.AwardEmoji;

import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.Response;
import java.util.List;

/**
 * This class implements the client side API for the GitLab Award Emoji API calls.
 *
 * @author Kimi Liu
 * @version 6.1.1
 * @see <a href="https://docs.gitlab.com/ce/api/award_emoji.html">GitLab Award Emoji API Documentaion</a>
 * @since v4.8.31
 * @since JDK 1.8+
 */
public class AwardEmojiApi extends AbstractApi {

    public AwardEmojiApi(GitLabApi gitLabApi) {
        super(gitLabApi);
    }

    /**
     * Get a list of award emoji for the specified issue.
     *
     * <pre><code>GitLab Endpoint: GET /projects/:id/issues/:issue_iid/award_emoji</code></pre>
     *
     * @param projectIdOrPath id, path of the project, or a Project instance holding the project ID or path
     * @param issueIid        the issue IID to get the award emojis for
     * @return a list of AwardEmoji for the specified issue
     * @throws GitLabApiException if any exception occurs
     */
    public List<AwardEmoji> getIssueAwardEmojis(Object projectIdOrPath, Integer issueIid) throws GitLabApiException {
        Response response = get(Response.Status.OK, getPageQueryParams(1, getDefaultPerPage()),
                "projects", getProjectIdOrPath(projectIdOrPath), "issues", issueIid, "award_emoji");
        return response.readEntity(new GenericType<List<AwardEmoji>>() {
        });
    }

    /**
     * Get a list of award emoji for the specified merge request.
     *
     * <pre><code>GitLab Endpoint: GET /projects/:id/merge_requests/:merge_request_iid/award_emoji</code></pre>
     *
     * @param projectIdOrPath id, path of the project, or a Project instance holding the project ID or path
     * @param mergeRequestIid the merge request IID to get the award emojis for
     * @return a list of AwardEmoji for the specified merge request
     * @throws GitLabApiException if any exception occurs
     */
    public List<AwardEmoji> getMergeRequestAwardEmojis(Object projectIdOrPath, Integer mergeRequestIid) throws GitLabApiException {
        Response response = get(Response.Status.OK, getPageQueryParams(1, getDefaultPerPage()),
                "projects", getProjectIdOrPath(projectIdOrPath), "merge_requests", mergeRequestIid, "award_emoji");
        return response.readEntity(new GenericType<List<AwardEmoji>>() {
        });
    }

    /**
     * Get a list of award emoji for the specified snippet.
     *
     * <pre><code>GitLab Endpoint: GET /projects/:id/snippets/:snippet_id/award_emoji</code></pre>
     *
     * @param projectIdOrPath id, path of the project, or a Project instance holding the project ID or path
     * @param snippetId       the snippet ID to get the award emojis for
     * @return a list of AwardEmoji for the specified snippet
     * @throws GitLabApiException if any exception occurs
     */
    public List<AwardEmoji> getSnippetAwardEmojis(Object projectIdOrPath, Integer snippetId) throws GitLabApiException {
        Response response = get(Response.Status.OK, getPageQueryParams(1, getDefaultPerPage()),
                "projects", getProjectIdOrPath(projectIdOrPath), "snippets", snippetId, "award_emoji");
        return response.readEntity(new GenericType<List<AwardEmoji>>() {
        });
    }

    /**
     * Get a list of award emoji for the specified note.
     *
     * <pre><code>GitLab Endpoint: GET /projects/:id/issues/:issue_iid/notes/:note_id/award_emoji</code></pre>
     *
     * @param projectIdOrPath id, path of the project, or a Project instance holding the project ID or path
     * @param issueIid        the issue IID of the issue that owns the note
     * @param noteId          the note ID to get the award emojis for
     * @return a list of AwardEmoji for the specified note
     * @throws GitLabApiException if any exception occurs
     */
    public List<AwardEmoji> getNoteAwardEmojis(Object projectIdOrPath, Integer issueIid, Integer noteId) throws GitLabApiException {
        Response response = get(Response.Status.OK, getPageQueryParams(1, getDefaultPerPage()),
                "projects", getProjectIdOrPath(projectIdOrPath), "issues", issueIid, "notes", noteId, "award_emoji");
        return response.readEntity(new GenericType<List<AwardEmoji>>() {
        });
    }

    /**
     * Get the specified award emoji for the specified issue.
     *
     * <pre><code>GitLab Endpoint: GET /projects/:id/issues/:issue_iid/award_emoji/:award_id</code></pre>
     *
     * @param projectIdOrPath id, path of the project, or a Project instance holding the project ID or path
     * @param issueIid        the issue IID to get the award emoji for
     * @param awardId         the ID of the award emoji to get
     * @return an AwardEmoji instance for the specified award emoji
     * @throws GitLabApiException if any exception occurs
     */
    public AwardEmoji getIssueAwardEmoji(Object projectIdOrPath, Integer issueIid, Integer awardId) throws GitLabApiException {
        Response response = get(Response.Status.OK, getPageQueryParams(1, getDefaultPerPage()),
                "projects", getProjectIdOrPath(projectIdOrPath), "issues", issueIid, "award_emoji", awardId);
        return (response.readEntity(AwardEmoji.class));
    }

    /**
     * Get the specified award emoji for the specified merge request.
     *
     * <pre><code>GitLab Endpoint: GET /projects/:id/merge_requests/:merge_request_iid/award_emoji/:award_id</code></pre>
     *
     * @param projectIdOrPath id, path of the project, or a Project instance holding the project ID or path
     * @param mergeRequestIid the merge request IID to get the award emoji for
     * @param awardId         the ID of the award emoji to get
     * @return an AwardEmoji instance for the specified award emoji
     * @throws GitLabApiException if any exception occurs
     */
    public AwardEmoji getMergeRequestAwardEmoji(Object projectIdOrPath, Integer mergeRequestIid, Integer awardId) throws GitLabApiException {
        Response response = get(Response.Status.OK, getPageQueryParams(1, getDefaultPerPage()),
                "projects", getProjectIdOrPath(projectIdOrPath), "merge_requests", mergeRequestIid, "award_emoji", awardId);
        return (response.readEntity(AwardEmoji.class));
    }

    /**
     * Get the specified award emoji for the specified snippet.
     *
     * <pre><code>GitLab Endpoint: GET /projects/:id/snippets/:snippet_id/award_emoji/:award_id</code></pre>
     *
     * @param projectIdOrPath id, path of the project, or a Project instance holding the project ID or path
     * @param snippetId       the snippet ID to get the award emoji for
     * @param awardId         the ID of the award emoji to get
     * @return an AwardEmoji instance for the specified award emoji
     * @throws GitLabApiException if any exception occurs
     */
    public AwardEmoji getSnippetAwardEmoji(Object projectIdOrPath, Integer snippetId, Integer awardId) throws GitLabApiException {
        Response response = get(Response.Status.OK, getPageQueryParams(1, getDefaultPerPage()),
                "projects", getProjectIdOrPath(projectIdOrPath), "snippets", snippetId, "award_emoji", awardId);
        return (response.readEntity(AwardEmoji.class));
    }

    /**
     * Get the specified award emoji for the specified note.
     *
     * <pre><code>GitLab Endpoint: GET /projects/:id/issues/:issue_iid/notes/:note_id/award_emoji/:award_id</code></pre>
     *
     * @param projectIdOrPath id, path of the project, or a Project instance holding the project ID or path
     * @param issueIid        the issue IID of the issue that owns the note
     * @param noteId          the note ID to get the award emoji from
     * @param awardId         the ID of the award emoji to get
     * @return an AwardEmoji instance for the specified award emoji
     * @throws GitLabApiException if any exception occurs
     */
    public AwardEmoji getNoteAwardEmoji(Object projectIdOrPath, Integer issueIid, Integer noteId, Integer awardId) throws GitLabApiException {
        Response response = get(Response.Status.OK, getPageQueryParams(1, getDefaultPerPage()),
                "projects", getProjectIdOrPath(projectIdOrPath), "issues", issueIid, "notes", noteId, "award_emoji", awardId);
        return (response.readEntity(AwardEmoji.class));
    }

    /**
     * Add an award emoji for the specified issue.
     *
     * <pre><code>GitLab Endpoint: POST /projects/:id/issues/:issue_iid/award_emoji</code></pre>
     *
     * @param projectIdOrPath id, path of the project, or a Project instance holding the project ID or path
     * @param issueIid        the issue IID to add the award emoji to
     * @param name            the name of the award emoji to add
     * @return an AwardEmoji instance for the added award emoji
     * @throws GitLabApiException if any exception occurs
     */
    public AwardEmoji addIssueAwardEmoji(Object projectIdOrPath, Integer issueIid, String name) throws GitLabApiException {
        GitLabApiForm form = new GitLabApiForm().withParam("name", name, true);
        Response response = post(Response.Status.CREATED, form.asMap(),
                "projects", getProjectIdOrPath(projectIdOrPath), "issues", issueIid, "award_emoji");
        return (response.readEntity(AwardEmoji.class));
    }

    /**
     * Add an award emoji to the specified merge request.
     *
     * <pre><code>GitLab Endpoint: POST /projects/:id/merge_requests/:merge_request_iid/award_emoji</code></pre>
     *
     * @param projectIdOrPath id, path of the project, or a Project instance holding the project ID or path
     * @param mergeRequestIid the merge request IID to add the award emoji to
     * @param name            the name of the award emoji to add
     * @return an AwardEmoji instance for the added award emoji
     * @throws GitLabApiException if any exception occurs
     */
    public AwardEmoji addMergeRequestAwardEmoji(Object projectIdOrPath, Integer mergeRequestIid, String name) throws GitLabApiException {
        GitLabApiForm form = new GitLabApiForm().withParam("name", name, true);
        Response response = post(Response.Status.CREATED, form.asMap(),
                "projects", getProjectIdOrPath(projectIdOrPath), "merge_requests", mergeRequestIid, "award_emoji");
        return (response.readEntity(AwardEmoji.class));
    }

    /**
     * Add an award emoji to the specified snippet.
     *
     * <pre><code>GitLab Endpoint: POST /projects/:id/snippets/:snippet_id/award_emoji</code></pre>
     *
     * @param projectIdOrPath id, path of the project, or a Project instance holding the project ID or path
     * @param snippetId       the snippet ID to add the award emoji to
     * @param name            the name of the award emoji to add
     * @return an AwardEmoji instance for the added award emoji
     * @throws GitLabApiException if any exception occurs
     */
    public AwardEmoji addSnippetAwardEmoji(Object projectIdOrPath, Integer snippetId, String name) throws GitLabApiException {
        GitLabApiForm form = new GitLabApiForm().withParam("name", name, true);
        Response response = post(Response.Status.CREATED, form.asMap(),
                "projects", getProjectIdOrPath(projectIdOrPath), "snippets", snippetId, "award_emoji");
        return (response.readEntity(AwardEmoji.class));
    }

    /**
     * Add an award emoji for the specified note.
     *
     * <pre><code>GitLab Endpoint: POST /projects/:id/issues/:issue_iid/notes/:noteId/award_emoji</code></pre>
     *
     * @param projectIdOrPath id, path of the project, or a Project instance holding the project ID or path
     * @param issueIid        the issue IID of the issue that owns the note
     * @param noteId          the note ID to add the award emoji to
     * @param name            the name of the award emoji to add
     * @return an AwardEmoji instance for the added award emoji
     * @throws GitLabApiException if any exception occurs
     */
    public AwardEmoji addNoteAwardEmoji(Object projectIdOrPath, Integer issueIid, Integer noteId, String name) throws GitLabApiException {
        GitLabApiForm form = new GitLabApiForm().withParam("name", name, true);
        Response response = post(Response.Status.CREATED, form.asMap(),
                "projects", getProjectIdOrPath(projectIdOrPath), "issues", issueIid, "notes", noteId, "award_emoji");
        return (response.readEntity(AwardEmoji.class));
    }

    /**
     * Delete an award emoji from the specified issue.
     *
     * <pre><code>GitLab Endpoint: DELETE /projects/:id/issues/:issue_iid/award_emoji/:award_id</code></pre>
     *
     * @param projectIdOrPath id, path of the project, or a Project instance holding the project ID or path
     * @param issueIid        the issue IID to delete the award emoji from
     * @param awardId         the ID of the award emoji to delete
     * @throws GitLabApiException if any exception occurs
     */
    public void deleteIssueAwardEmoji(Object projectIdOrPath, Integer issueIid, Integer awardId) throws GitLabApiException {
        delete(Response.Status.NO_CONTENT, null,
                "projects", getProjectIdOrPath(projectIdOrPath), "issues", issueIid, "award_emoji", awardId);
    }

    /**
     * Delete an award emoji from the specified merge request.
     *
     * <pre><code>GitLab Endpoint: DELETE /projects/:id/merge_requests/:merge_request_iid/award_emoji/:award_id</code></pre>
     *
     * @param projectIdOrPath id, path of the project, or a Project instance holding the project ID or path
     * @param mergeRequestIid the merge request IID to delete the award emoji from
     * @param awardId         the ID of the award emoji to delete
     * @throws GitLabApiException if any exception occurs
     */
    public void deleteMergeRequestAwardEmoji(Object projectIdOrPath, Integer mergeRequestIid, Integer awardId) throws GitLabApiException {
        delete(Response.Status.NO_CONTENT, null,
                "projects", getProjectIdOrPath(projectIdOrPath), "merge_requests", mergeRequestIid, "award_emoji", awardId);
    }

    /**
     * Delete an award emoji from the specified snippet.
     *
     * <pre><code>GitLab Endpoint: DELETE /projects/:id/snippets/:snippet_id/award_emoji/:award_id</code></pre>
     *
     * @param projectIdOrPath id, path of the project, or a Project instance holding the project ID or path
     * @param snippetId       the snippet ID to delete the award emoji from
     * @param awardId         the ID of the award emoji to delete
     * @throws GitLabApiException if any exception occurs
     */
    public void deleteSnippetAwardEmoji(Object projectIdOrPath, Integer snippetId, Integer awardId) throws GitLabApiException {
        delete(Response.Status.NO_CONTENT, null,
                "projects", getProjectIdOrPath(projectIdOrPath), "snippets", snippetId, "award_emoji", awardId);
    }

    /**
     * Delete an award emoji from the specified note.
     *
     * <pre><code>GitLab Endpoint: DELETE /projects/:id/issues/:issue_iid/notes/:note_id/award_emoji/:award_id</code></pre>
     *
     * @param projectIdOrPath id, path of the project, or a Project instance holding the project ID or path
     * @param issueIid        the issue IID that owns the note
     * @param noteId          the note ID of the note to delete the award emoji from
     * @param awardId         the ID of the award emoji to delete
     * @throws GitLabApiException if any exception occurs
     */
    public void deleteNoteAwardEmoji(Object projectIdOrPath, Integer issueIid, Integer noteId, Integer awardId) throws GitLabApiException {
        delete(Response.Status.NO_CONTENT, null,
                "projects", getProjectIdOrPath(projectIdOrPath), "issues", issueIid, "notes", noteId, "award_emoji", awardId);
    }

}
