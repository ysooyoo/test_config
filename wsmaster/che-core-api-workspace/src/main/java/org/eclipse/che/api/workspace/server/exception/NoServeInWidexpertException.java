/*
 * Copyright (c) 2012-2017 Red Hat, Inc.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Red Hat, Inc. - initial API and implementation
 */
package org.eclipse.che.api.workspace.server.exception;

import java.util.Map;
import org.eclipse.che.api.core.ServerException;
import org.eclipse.che.api.core.rest.shared.dto.ExtendedError;
import org.eclipse.che.dto.server.DtoFactory;

/**
 * Should be thrown when limit of some resource is exceeded e.g. the ram per workspace.
 *
 * @author Yevhenii Voevodin
 */
public class NoServeInWidexpertException extends ServerException {

  public NoServeInWidexpertException(String message) {
    super(message);
  }

  public NoServeInWidexpertException(String message, Map<String, String> attributes) {
    super(
        DtoFactory.newDto(ExtendedError.class)
            .withMessage(message)
            .withAttributes(attributes)
            .withErrorCode(ErrorCodes.NO_SERVE_IN_WIDEXPERT));
  }
}
